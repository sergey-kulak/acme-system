import { useEffect, useState, useCallback } from 'react';
import { connect } from "react-redux";
import { useIntl } from 'react-intl';
import * as Icon from 'react-feather';
import { Link, useHistory, useLocation } from "react-router-dom";
import planService from './planService';
import Pagination from '../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils';
import ShowFilterButton from '../common/ShowFilterButton';
import SortColumn from '../common/SortColumn';
import { onSuccess, onError } from '../common/toastNotification'
import PlanStatusLabel from './PlanStatusLabel';
import PlanFilter from './PlanFilter';
import ChangePlanStatusDialog from './ChangePlanStatusDialog';

function PlanDashboard({ onSuccess, onError }) {
    const history = useHistory();
    const intl = useIntl();
    const query = new URLSearchParams(useLocation().search);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query));
    const [showFilter, setShowFilter] = useState(false);
    const [showStatusChangeDialog, setShowStatusChangeDialog] = useState(false);
    const [modifiedPlan, setModifiedPlan] = useState();

    const loadData = useCallback(() => {
        return planService.find(filter, pageable, sort)
            .then(response => setPage(response.data))
    }, [filter, pageable, sort]);

    useEffect(() => {
        loadData()
            .then(() => {
                history.replace(combineAsUrlParams(filter, pageable, sort));
            });
    }, [pageable, sort, filter, history, loadData]);

    function onPageableChange(page) {
        setPageable(page);
    }

    function onSortChange(sort) {
        setSort(sort);
    }

    function onFilterChange(filter) {
        setFilter(filter);
    }

    function onStatusClick(e, modifiedPlan) {
        e.preventDefault();
        setModifiedPlan(modifiedPlan);
        setShowStatusChangeDialog(true);
    }

    function onStatusChange(newStatus) {
        setShowStatusChangeDialog(false);
        if (newStatus) {
            planService.changeStatus(modifiedPlan.id, { status: newStatus })
                .then(() => {
                    onSuccess("Plan status was changed successfully");
                    loadData();
                }, error => {
                    onError(error.response.data);
                });
        }
    }

    function formatCountries(plan) {
        return plan.countries &&
            plan.countries.map(country => intl.formatMessage({ id: `country.${country}` })).join(', ')
    }

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Plans
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
                <Link to="/plans/new" className="btn btn-light cmt-btn ml-2">
                    <Icon.PlusSquare className="filter-icon" />
                </Link>
            </div>

            {showFilter && <PlanFilter filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th className="status-th" />
                            <SortColumn field="name" name="Name" sort={sort} onClick={onSortChange} />
                            <SortColumn field="max_table_count" name="Count" sort={sort} onClick={onSortChange} />
                            <th>Price</th>
                            <th>Discount (6m)</th>
                            <th>Discount (1y)</th>
                            <th>Countries</th>
                            <th>Companies</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(plan => <tr key={plan.id}>
                                <td className="align-middle">
                                    {plan.status === 'STOPPED' ?
                                        <PlanStatusLabel status={plan.status} /> :
                                        <a href="#status" onClick={e => onStatusClick(e, plan)}>
                                            <PlanStatusLabel status={plan.status} />
                                        </a>
                                    }
                                </td>
                                <td>
                                    <Link to={`/plans/${plan.id}`}>
                                        {plan.name}
                                    </Link>
                                </td>
                                <td>{plan.maxTableCount}</td>
                                <td>{plan.monthPrice} {plan.currency}</td>
                                <td>{plan.upfrontDiscount6m ? `${plan.upfrontDiscount6m}%` : ''}</td>
                                <td>{plan.upfrontDiscount1y ? `${plan.upfrontDiscount1y}%` : ''}</td>
                                <td>{formatCountries(plan)}</td>
                                <td>{plan.companyCount}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>
            <div>
                {showStatusChangeDialog
                    && <ChangePlanStatusDialog show={showStatusChangeDialog}
                        status={modifiedPlan.status} onClose={onStatusChange}
                    />
                }
            </div>
        </div>
    );
}

class Filter {
    static URL_PARAM_NAME_PATTERN = 'pn';
    static URL_PARAM_TABLE_COUNT = 'tc';
    static URL_PARAM_COUNTRY = 'cn';
    static URL_PARAM_ONLY_GLOBAL = 'og';
    static URL_PARAM_STATUS = 'st';

    constructor(namePattern, tableCount, country, onlyGlobal, status) {
        this.namePattern = namePattern;
        this.tableCount = tableCount;
        this.country = country;
        this.onlyGlobal = onlyGlobal;
        this.status = status;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.namePattern, this.tableCount,
            this.country, this.onlyGlobal, this.status);
        newFilter[field] = value;
        return newFilter;
    }

    toUrlParams() {
        return {
            [Filter.URL_PARAM_NAME_PATTERN]: this.namePattern,
            [Filter.URL_PARAM_TABLE_COUNT]: this.tableCount,
            [Filter.URL_PARAM_COUNTRY]: this.country,
            [Filter.URL_PARAM_ONLY_GLOBAL]: this.onlyGlobal,
            [Filter.URL_PARAM_STATUS]: this.status,
        }
    }

    static fromUrlParams(urlSearchParams) {
        let urlStatuses = urlSearchParams.getAll(Filter.URL_PARAM_STATUS);
        return new Filter(
            urlSearchParams.get(Filter.URL_PARAM_NAME_PATTERN) || '',
            urlSearchParams.get(Filter.URL_PARAM_TABLE_COUNT) || '',
            urlSearchParams.get(Filter.URL_PARAM_COUNTRY) || '',
            urlSearchParams.get(Filter.URL_PARAM_ONLY_GLOBAL) || '',
            urlStatuses && urlStatuses.length ? urlStatuses : ['ACTIVE', 'INACTIVE'],
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(PlanDashboard);