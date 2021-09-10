import { useCallback, useEffect, useState } from 'react';
import * as Icon from 'react-feather';
import { FormattedMessage } from 'react-intl';
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import { GLOBAL_CACHE } from '../common/cache';
import Pagination from '../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils';
import { hasRole, ROLE } from "../common/security";
import ShowFilterButton from '../common/ShowFilterButton';
import SortColumn from '../common/SortColumn';
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage } from '../common/utils';
import companyService from '../company/companyService';
import planService from '../plan/planService';
import publicPointPlanService from '../plan/publicPointPlanService';
import ChangePublicPointStatusDialog from './ChangePublicPointStatusDialog';
import PublicPointFilter from './PublicPointFilter';
import publicPointService from './publicPointService';
import PublicPointStatusLabel from './PublicPointStatusLabel';

const cache = GLOBAL_CACHE;
const PLAN_REGION = 'pp-dash-plan-region';

function PublicPointDashboard({ auth }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);
    const isAdmin = hasRole(auth, ROLE.ADMIN);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth));
    const [showFilter, setShowFilter] = useState(false);
    const [companyNames, setCompanyNames] = useState({});
    const [modifiedPublicPoint, setModifiedPublicPoint] = useState();
    const [showStatusChangeDialog, setShowStatusChangeDialog] = useState(false);

    const loadPlans = useCallback((newPage) =>  {
        let promises = newPage.content
            .map(pp => publicPointPlanService.findActivePlanId(pp.id)
                .then(response => response.data)
                .then(getPlanInCache)
                .then(plan => pp.plan = plan)
            );

        return Promise.all(promises)
            .then(() => newPage);
    }, []);

    const loadData = useCallback(() => {
        return publicPointService.find(filter, pageable, sort)
            .then(response => response.data)
            .then(loadPlans)
            .then(setPage);
    }, [filter, pageable, sort, loadPlans]);

    useEffect(() => {
        loadData()
            .then(() => {
                let filterUrlParams = filter.toUrlParams(auth);
                history.replace(combineAsUrlParams(filterUrlParams, pageable, sort));
            });
    }, [pageable, sort, filter, history, auth, loadData]);

    useEffect(() => {
        if (isAdmin) {
            companyService.findNames()
                .then(response => {
                    let data = response.data.reduce((acc, item) => {
                        acc[item.id] = item.fullName;
                        return acc;
                    }, {})
                    setCompanyNames(data);
                });
        }
    }, [isAdmin]);

    function getPlanInCache(planId) {
        return planId ? cache.retriveIfAbsent(PLAN_REGION, planId,
            () => planService.findById(planId), 300)
            .then(response => response.data) : undefined;
    }

    function onPublicPointStatusClick(e, pp) {
        e.preventDefault();
        setModifiedPublicPoint(pp);
        setShowStatusChangeDialog(true);
    }

    function onStatusChange(newStatus) {
        setShowStatusChangeDialog(false);
        if (newStatus) {
            publicPointService.changeStatus(modifiedPublicPoint.id, { status: newStatus })
                .then(() => {
                    onSuccess("Public point status was changed successfully");
                    loadData();
                }, error => {
                    onError(getErrorMessage(error.response.data));
                });
        }
    }


    function onPageableChange(page) {
        setPageable(page);
    }

    function onSortChange(sort) {
        setSort(sort);
    }

    function onFilterChange(filter) {
        setFilter(filter);
    }

    function getPlan(pp) {
        return pp.plan ? pp.plan.name : ''
    }

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Public points
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
                <Link to="/public-points/new" className="btn btn-light cmt-btn ml-2">
                    <Icon.PlusSquare className="filter-icon" />
                </Link>
            </div>

            {showFilter && <PublicPointFilter isAdmin={isAdmin} filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th className="status-th" />
                            <SortColumn field="name" name="Name" sort={sort} onClick={onSortChange} />
                            <th>City</th>
                            <th>Address</th>
                            <th>Primary lang.</th>
                            {isAdmin && <th>Company</th>}
                            <th>Plan</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(pp => <tr key={pp.id}>
                                <td className="align-middle">
                                    {pp.status === 'STOPPED' ?
                                        <PublicPointStatusLabel status={pp.status} /> :
                                        <a href="#status" onClick={e => onPublicPointStatusClick(e, pp)}>
                                            <PublicPointStatusLabel status={pp.status} />
                                        </a>
                                    }
                                </td>
                                <td>
                                    <Link to={`/public-point-view/${pp.id}`}>
                                        {pp.name}
                                    </Link>
                                </td>
                                <td>{pp.city}</td>
                                <td>{pp.address}</td>
                                <td>
                                    <FormattedMessage id={`lang.${pp.primaryLang}`} />
                                </td>
                                {isAdmin && <td>
                                    <Link to={`/company-view/${pp.companyId}`}>
                                        {companyNames[pp.companyId]}
                                    </Link>
                                </td>}
                                <td>{getPlan(pp)}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>
            {<div>
                {showStatusChangeDialog
                    && <ChangePublicPointStatusDialog show={showStatusChangeDialog}
                        isAdmin={isAdmin}
                        status={modifiedPublicPoint.status} onClose={onStatusChange}
                    />
                }
            </div>}
        </div>
    )
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp';
    static URL_PARAM_NAME_PATTERN = 'np';
    static URL_PARAM_STATUS = 'st';

    constructor(namePattern, companyId, status) {
        this.namePattern = namePattern;
        this.companyId = companyId;
        this.status = status;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.namePattern, this.companyId, this.status);
        newFilter[field] = value;
        return newFilter;
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_NAME_PATTERN]: this.namePattern,
            [Filter.URL_PARAM_STATUS]: this.status,
        };

        if (!hasRole(auth, ROLE.ADMIN)) {
            delete urlData[Filter.URL_PARAM_COMPANY_ID];
        }
        return { toUrlParams: () => urlData };
    }

    static fromUrlParams(urlSearchParams, auth) {
        let companyId = hasRole(auth, ROLE.ADMIN) ?
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '' :
            auth.user.cmpid;

        let urlStatuses = urlSearchParams.getAll(Filter.URL_PARAM_STATUS);
        return new Filter(
            urlSearchParams.get(Filter.URL_PARAM_NAME_PATTERN) || '',
            companyId,
            urlStatuses && urlStatuses.length ? urlStatuses : ['INACTIVE', 'ACTIVE', 'SUSPENDED']
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(PublicPointDashboard);