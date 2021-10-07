import moment from "moment";
import { useCallback, useEffect, useState } from "react";
import { FormattedMessage } from "react-intl";
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import Pagination from "../common/Pagination";
import { combineAsUrlParams, Pageable, Sort } from "../common/paginationUtils";
import { hasRole, ROLE } from "../common/security";
import ShowFilterButton from "../common/ShowFilterButton";
import SortColumn from "../common/SortColumn";
import { onSuccess } from '../common/toastNotification';
import publicPointService from "../public-point/publicPointService";
import OrderFilter from "./OrderFilter";
import orderService from "./orderService";

function OrderDashboard({ auth }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'created_date', 'desc'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth));
    const [showFilter, setShowFilter] = useState(true);
    const [publicPoint, setPublicPoint] = useState();

    const loadData = useCallback(() => {
        return orderService.find(filter, pageable, sort)
            .then(response => response.data)
            .then(setPage)
    }, [filter, pageable, sort])

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            loadData()
                .then(() => {
                    let filterUrlParams = filter.toUrlParams(auth);
                    history.replace(combineAsUrlParams(filterUrlParams, pageable, sort));
                });;
        } else {
            setPage({ content: [] });
        }
    }, [filter, pageable, sort, history, auth, loadData]);

    useEffect(() => {
        if (filter.publicPointId) {
            publicPointService.findByIdFullDetails(filter.publicPointId)
                .then(response => response.data)
                .then(setPublicPoint);
        }
    }, [filter.publicPointId]);

    function onPageableChange(page) {
        setPageable(page);
    }

    function onSortChange(sort) {
        setSort(sort);
    }

    function onFilterChange(filter) {
        setFilter(filter);
    }

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Orders
                </div>
                {!auth.user.ppid && <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />}
            </div>

            {showFilter && <OrderFilter auth={auth} filter={filter} onChange={onFilterChange} />}

            {filter.companyId && filter.publicPointId && <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th>Number</th>
                            <th>Status</th>
                            <SortColumn field="created_date" name="Created" sort={sort} onClick={onSortChange} />
                            <th>Dish count</th>
                            <SortColumn field="total_price"
                                name={'Total price' + (publicPoint ? `, ${publicPoint.currency}` : '')}
                                sort={sort} onClick={onSortChange} />                            
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(order => <tr key={order.id}>
                                <td>
                                    <Link to={`/orders/${order.id}`}>{order.number}</Link>
                                </td>
                                <td>
                                    <FormattedMessage id={`order.status.${order.status.toLowerCase()}`} />
                                </td>
                                <td>
                                    {moment(new Date(order.createdDate)).format('yyyy-MM-DD HH:mm:ss')}
                                </td>
                                <td>{order.dishCount}</td>
                                <td>{order.totalPrice}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>}
        </div>
    );
}

function getDefaultFromCreatedDate() {
    return moment().subtract(30, "days").format("yyyy-MM-DD");
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp';
    static URL_PARAM_PP_ID = 'pp';
    static URL_PARAM_ORDER_NUMBER = 'on';
    static URL_PARAM_STATUS = 'st';
    static URL_PARAM_FROM_PRICE = 'fp';
    static URL_PARAM_TO_PRICE = 'tp';
    static URL_PARAM_FROM_DATE = 'fd';
    static URL_PARAM_TO_DATE = 'td';
    static URL_PARAM_DISH_ID = 'di';

    constructor(companyId, publicPointId, number, status,
        fromTotalPrice, toTotalPrice, fromCreatedDate, toCreatedDate,
        dishId) {
        this.publicPointId = publicPointId;
        this.companyId = companyId;
        this.number = number;
        this.status = status;
        this.fromTotalPrice = fromTotalPrice;
        this.toTotalPrice = toTotalPrice;
        this.fromCreatedDate = fromCreatedDate;
        this.toCreatedDate = toCreatedDate;
        this.dishId = dishId;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.publicPointId,
            this.number, this.status, this.fromTotalPrice, this.toTotalPrice,
            this.fromCreatedDate, this.toCreatedDate, this.dishId);
        newFilter[field] = value;
        if (field === 'companyId') {
            newFilter.publicPointId = '';
        }
        if (field === 'companyId' || field === 'publicPointId') {
            newFilter.number = '';
            newFilter.status = '';
            newFilter.fromTotalPrice = '';
            newFilter.toTotalPrice = '';
            newFilter.fromCreatedDate = '';
            newFilter.toCreatedDate = '';
            newFilter.dishId = '';
        }
        if (field === 'publicPointId' && newFilter.publicPointId && !newFilter.fromCreatedDate) {
            newFilter.fromCreatedDate = getDefaultFromCreatedDate();
        }
        return newFilter;
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_PP_ID]: this.publicPointId,
            [Filter.URL_PARAM_ORDER_NUMBER]: this.number,
            [Filter.URL_PARAM_STATUS]: this.status,
            [Filter.URL_PARAM_FROM_PRICE]: this.fromTotalPrice,
            [Filter.URL_PARAM_TO_PRICE]: this.toTotalPrice,
            [Filter.URL_PARAM_FROM_DATE]: this.fromCreatedDate,
            [Filter.URL_PARAM_TO_DATE]: this.toCreatedDate,
            [Filter.URL_PARAM_DISH_ID]: this.dishId
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
        return new Filter(companyId, ppId,
            urlSearchParams.get(Filter.URL_PARAM_ORDER_NUMBER) || '',
            urlSearchParams.get(Filter.URL_PARAM_STATUS) || '',
            urlSearchParams.get(Filter.URL_PARAM_FROM_PRICE) || '',
            urlSearchParams.get(Filter.URL_PARAM_TO_PRICE) || '',
            urlSearchParams.get(Filter.URL_PARAM_FROM_DATE) || '',
            urlSearchParams.get(Filter.URL_PARAM_TO_DATE) || '',
            urlSearchParams.get(Filter.URL_PARAM_DISH_ID) || '');
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess
})(OrderDashboard);