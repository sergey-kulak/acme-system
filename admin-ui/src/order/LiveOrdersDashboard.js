import React, { useCallback, useEffect, useReducer, useState } from "react";
import * as Icon from 'react-feather';
import { FormattedMessage, useIntl } from 'react-intl';
import { connect } from "react-redux";
import { useHistory, useLocation } from "react-router";
import { Link } from "react-router-dom";
import { combineAsUrlParams } from "../common/paginationUtils";
import { setOnline } from '../common/rsocket';
import { hasRole, ROLE } from "../common/security";
import ShowFilterButton from "../common/ShowFilterButton";
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage, isEmptyObject } from "../common/utils";
import publicPointNotificationService from "../public-point/publicPointNotificationService";
import publicPointTableService from "../public-point/publicPointTableService";
import LiveOrdersFilter from "./LiveOrdersFilter";
import { reducer } from './liveOrdersReducer';
import orderService from "./orderService";

const initialState = {
    tableOrders: [],
    tableNames: []
}

function LiveOrdersDashboard({ auth, onSuccess, onError, setOnline }) {
    const history = useHistory();
    const intl = useIntl();
    const query = new URLSearchParams(useLocation().search);

    const [state, dispatch] = useReducer(reducer, initialState);
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth));
    const [showFilter, setShowFilter] = useState(true);

    const loadData = useCallback(() => {
        return orderService.findLive(filter)
            .then(response =>
                dispatch({ type: 'set-orders', payload: response.data }))
    }, [filter])

    useEffect(() => {
        if (filter.companyId && filter.publicPointId
            && !isEmptyObject(state.tableNames)) {
            loadData()
                .then(() => {
                    history.replace(combineAsUrlParams(filter.toUrlParams(auth)));
                });
        } else {
            dispatch({ type: 'set-orders', payload: [] });
        }
    }, [filter, history, auth, state.tableNames, loadData]);

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            publicPointTableService.find(filter.publicPointId)
                .then(response =>
                    dispatch({ type: 'set-table-names', payload: response.data }))
        } else {
            dispatch({ type: 'set-table-names', payload: [] })
        }
    }, [filter]);

    const processEvent = useCallback((response) => {
        let event = response.data
        console.log(response.data);
        switch (event.type) {
            case 'OrderItemStatusChangedEvent':
                switch (event.data.toStatus) {
                    case 'IN_PROGRESS':
                    case 'DONE':
                    case 'DECLINED':
                        updateItemStatus(event.data.itemId, event.data.toStatus);
                        break;
                    default:
                }
                break;
            case 'OrderStatusChangedEvent':
                switch (event.data.toStatus) {
                    case 'CONFIRMED':
                    case 'DELIVERED':
                    case 'IN_PROGRESS':
                    case 'READY':
                        updateOrderStatus(event.data.orderId, event.data.toStatus);
                        break;
                    case 'DECLINED':
                    case 'PAID':
                        removeOrder(event.data.orderId);
                        break;
                    default:
                }
                break;
            case 'OrderCreatedEvent':
                const orderId = event.data.orderId;
                orderService.findById(orderId)
                    .then(response =>
                        dispatch({ type: 'add-order', payload: response.data }))
                break;
            default:
                console.log(`Unknown type: ${event.type}`);
        }
    }, [])

    const subscribe = useCallback((subscription) => {
        const notPpUser = !auth.user.ppid
        subscription.subscribe({
            onComplete: () => {
                if (notPpUser) {
                    setOnline(false)
                }
            },
            onError: error => {
                console.error(error);
                if (notPpUser) {
                    setOnline(false)
                }
            },
            onNext: response => processEvent(response),
            onSubscribe: sub => {
                if (notPpUser) {
                    setOnline(true)
                }
                sub.request(2147483647);
            }
        });
    }, [auth, processEvent, setOnline])

    useEffect(() => {
        const cmpId = filter.companyId
        const ppId = filter.publicPointId
        if (cmpId && ppId) {
            if (!publicPointNotificationService.isConnected(cmpId, ppId)) {
                publicPointNotificationService.close()
            }
            publicPointNotificationService
                .connect(auth.accessToken, cmpId, ppId)
                .then(subscribe);
        } else {
            publicPointNotificationService.close()
        }
    }, [filter, subscribe, auth]);

    function updateOrderStatus(orderId, status) {
        dispatch({ type: 'update-order-status', payload: { orderId, status } });
    }

    function removeOrder(orderId) {
        dispatch({ type: 'remove-order', payload: orderId });
    }

    function updateItemStatus(itemId, status) {
        dispatch({ type: 'update-item-status', payload: { itemId, status } });
    }

    function onFilterChange(filter) {
        setFilter(filter);
    }

    function confirmOrder(order) {
        changeOrderStatus(order, 'CONFIRMED', `${order.number} was confirmed successfully`,
            () => updateOrderStatus(order.id, 'CONFIRMED'));
    }

    function declineOrder(order) {
        changeOrderStatus(order, 'DECLINED', `${order.number} was declined successfully`,
            () => removeOrder(order.id));
    }

    function deliverOrder(order) {
        changeOrderStatus(order, 'DELIVERED', `${order.number} was delivered successfully`,
            () => updateOrderStatus(order.id, 'DELIVERED'));
    }

    function payOrder(order) {
        changeOrderStatus(order, 'PAID', `${order.number} was paid successfully`,
            () => removeOrder(order.id));
    }

    function changeOrderStatus(order, status, successMessage, callback) {
        orderService.changeOrderStatus(order.id, status)
            .then(() => {
                onSuccess(successMessage);
                if (callback) {
                    callback()
                }
            }, error => {
                onError(getErrorMessage(error.response.data));
            });
    }

    function inProgressItem(item) {
        changeItemStatus(item, 'IN_PROGRESS', 'Item was set in progress',
            () => updateItemStatus(item.id, 'IN_PROGRESS'));
    }

    function changeItemStatus(item, status, successMessage, callback) {
        orderService.changeItemStatus(item.id, status)
            .then(() => {
                onSuccess(successMessage);
                if (callback) {
                    callback()
                }
            }, error => {
                onError(getErrorMessage(error.response.data));
            });
    }

    function declineItem(item) {
        changeItemStatus(item, 'DECLINED', 'Item was declined',
            () => updateItemStatus(item.id, 'DECLINED'))
    }

    function doneItem(item) {
        changeItemStatus(item, 'DONE', 'Item was done',
            () => updateItemStatus(item.id, 'DONE'))
    }

    function orderCell(order) {
        let statusLabel = intl.formatMessage({ id: `order.status.${order.status.toLowerCase()}` })
        return <div className="d-flex align-items-center">
            <span><Link to={`/orders/${order.id}`}>{order.number}</Link> ({statusLabel})</span>
            {order.status === 'CREATED' && <>
                <button type="button"
                    className="btn btn-light cmt-small-btn ml-2"
                    onClick={e => confirmOrder(order)}>
                    <Icon.CheckSquare className="filter-icon" />
                </button>
                <button type="button"
                    className="btn btn-light cmt-small-btn ml-2"
                    onClick={e => declineOrder(order)}>
                    <Icon.XSquare className="filter-icon" />
                </button>
            </>}
            {order.status === 'READY' && <button type="button"
                className="btn btn-light cmt-small-btn ml-2"
                onClick={e => deliverOrder(order)}>
                <Icon.Truck className="filter-icon" />
            </button>}
            {order.status === 'DELIVERED' && <button type="button"
                className="btn btn-light cmt-small-btn ml-2"
                onClick={e => payOrder(order)}>
                <Icon.DollarSign className="filter-icon" />
            </button>}
        </div>;
    }

    function buttonsCell(order, item) {
        let inWorkOrder = order.status === 'CONFIRMED'
            || order.status === 'IN_PROGRESS';
        return inWorkOrder && <div>
            {item.status === 'CREATED' && <>
                <button type="button" className="btn btn-light cmt-small-btn"
                    onClick={e => inProgressItem(item)}>
                    <Icon.Play className="filter-icon" />
                </button>
                <button type="button" className="btn btn-light cmt-small-btn ml-2"
                    onClick={e => declineItem(item)}>
                    <Icon.XSquare className="filter-icon" />
                </button>
            </>}
            {item.status === 'IN_PROGRESS' && <>
                <button type="button" className="btn btn-light cmt-small-btn"
                    onClick={e => doneItem(item)}>
                    <Icon.CheckCircle className="filter-icon" />
                </button>
                <button type="button" className="btn btn-light cmt-small-btn ml-2"
                    onClick={e => declineItem(item)}>
                    <Icon.XSquare className="filter-icon" />
                </button>
            </>}
        </div >
    }

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Live orders
                </div>
                {!auth.user.ppid && <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />}
            </div>

            {showFilter && <LiveOrdersFilter auth={auth} filter={filter} onChange={onFilterChange} />}

            {filter.companyId && filter.publicPointId && <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md live-orders">
                    <thead>
                        <tr>
                            <th style={{ width: '15%' }}>Table</th>
                            <th style={{ width: '25%' }}>Order number</th>
                            <th style={{ width: '30%' }}>Dish</th>
                            <th style={{ width: '5%' }}>Qnt.</th>
                            <th style={{ width: '13%' }}>Status</th>
                            <th style={{ width: '12%' }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            state.tableOrders.map((toItem, toIndex) =>
                                <React.Fragment key={toItem.tableId}>
                                    {
                                        toItem.orders.map((order, ordIndex) =>
                                            <React.Fragment key={ordIndex}>
                                                {
                                                    order.items.map((item, iIndex) =>
                                                        <tr key={item.id}>
                                                            <td>{ordIndex === 0 && iIndex === 0 && toItem.tableName}</td>
                                                            <td>{iIndex === 0 && orderCell(order)}</td>
                                                            <td>{item.dishName}</td>
                                                            <td>{item.quantity}</td>
                                                            <td>
                                                                <FormattedMessage id={`orderitem.status.${item.status.toLowerCase()}`} />
                                                            </td>
                                                            <td>{buttonsCell(order, item)}</td>
                                                        </tr>
                                                    )
                                                }
                                            </React.Fragment>
                                        )
                                    }
                                </React.Fragment>
                            )
                        }
                    </tbody>
                </table>
            </div>}
        </div>
    );
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp';
    static URL_PARAM_PP_ID = 'pp';

    constructor(companyId, publicPointId) {
        this.publicPointId = publicPointId;
        this.companyId = companyId;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.publicPointId);
        newFilter[field] = value;
        if (field === 'companyId') {
            newFilter.publicPointId = '';
        }

        return newFilter;
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_PP_ID]: this.publicPointId
        };

        if (!hasRole(auth, ROLE.ADMIN)) {
            delete urlData[Filter.URL_PARAM_COMPANY_ID];
        }
        if (!hasRole(auth, ROLE.COMPANY_OWNER)) {
            delete urlData[Filter.URL_PARAM_PP_ID];
        }
        return { toUrlParams: () => urlData };
    }

    static fromUrlParams(urlSearchParams, auth) {
        let companyId = hasRole(auth, ROLE.ADMIN) ?
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '' :
            auth.user.cmpid;
        let ppId = hasRole(auth, ROLE.COMPANY_OWNER) ?
            urlSearchParams.get(Filter.URL_PARAM_PP_ID) || '' :
            auth.user.ppid;
        return new Filter(companyId, ppId);
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError, setOnline
})(LiveOrdersDashboard);