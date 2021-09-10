import { useCallback, useEffect, useState } from 'react';
import * as Icon from 'react-feather';
import { FormattedMessage } from 'react-intl';
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import { GLOBAL_CACHE } from '../common/cache';
import Pagination from '../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils';
import { hasExactRole, hasRole, ROLE } from "../common/security";
import ShowFilterButton from '../common/ShowFilterButton';
import SortColumn from '../common/SortColumn';
import { onError, onSuccess } from '../common/toastNotification';
import companyService from '../company/companyService';
import publicPointService from '../public-point/publicPointService';
import UserFilter from './UserFilter';
import userService from './userService';
import UserStatusLabel from './UserStatusLabel';

const cache = GLOBAL_CACHE;
const PP_REGION = 'usr-dash-pp-region';

function UserDashboard({ auth }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);
    const isAdmin = hasRole(auth, ROLE.ADMIN);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'last_name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth));
    const [showFilter, setShowFilter] = useState(false);
    const [companyNames, setCompanyNames] = useState({});

    const loadPublicPoints = useCallback((newPage) => {
        let promises = newPage.content
            .filter(user => !!user.publicPointId)
            .map(user => getPublicPointInCache(user.publicPointId)
                .then(pp => user.publicPoint = pp)
            );

        return Promise.all(promises)
            .then(() => newPage);
    }, []);

    const loadData = useCallback(() => {
        return userService.find(filter, pageable, sort)
            .then(response => response.data)
            .then(loadPublicPoints)
            .then(setPage);
    }, [filter, pageable, sort, loadPublicPoints]);

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

    function getPublicPointInCache(ppId) {
        return cache.retriveIfAbsent(PP_REGION, ppId,
            () => publicPointService.findById(ppId), 300)
            .then(response => response.data)
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

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Users
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
                <Link to="/users/new" className="btn btn-light cmt-btn ml-2">
                    <Icon.PlusSquare className="filter-icon" />
                </Link>
            </div>

            {showFilter && <UserFilter isAdmin={isAdmin} filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th className="status-th" />
                            <SortColumn field="last_name" name="Last name" sort={sort} onClick={onSortChange} />
                            <SortColumn field="first_name" name="First name" sort={sort} onClick={onSortChange} />
                            <th>Email</th>
                            <th>Role</th>
                            {isAdmin && <th>Company</th>}
                            <th>Public point</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(user => <tr key={user.id}>
                                <td className="align-middle">
                                    <UserStatusLabel status={user.status} />
                                </td>
                                <td>
                                    {hasExactRole(auth, ROLE.PP_MANAGER) && user.role === 'PP_MANAGER'
                                        && auth.user.id !== user.id ? user.lastName :
                                        <Link to={`/users/${user.id}`}>
                                            {user.lastName}
                                        </Link>}
                                </td>
                                <td>{user.firstName}</td>
                                <td>{user.email}</td>
                                <td>
                                    <FormattedMessage id={`user.role.${user.role.toLowerCase()}`} />
                                </td>
                                {isAdmin && <td>
                                    <Link to={`/company-view/${user.companyId}`}>
                                        {companyNames[user.companyId]}
                                    </Link>
                                </td>}
                                <td>{user.publicPoint && user.publicPoint.name}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>
        </div>
    )
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp';
    static URL_PARAM_EMAIL = 'em';
    static URL_PARAM_ROLE = 'rl';
    static URL_PARAM_STATUS = 'st';

    constructor(companyId, email, role, status, publicPointId) {
        this.companyId = companyId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.publicPointId = publicPointId;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.email, this.role,
            this.status, this.publicPointId);
        newFilter[field] = value;
        return newFilter;
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_EMAIL]: this.email,
            [Filter.URL_PARAM_ROLE]: this.role,
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
        let ppId = hasRole(auth, ROLE.COMPANY_OWNER) ? undefined : auth.user.ppid;

        return new Filter(
            companyId,
            urlSearchParams.get(Filter.URL_PARAM_EMAIL) || '',
            urlSearchParams.getAll(Filter.URL_PARAM_ROLE),
            urlSearchParams.getAll(Filter.URL_PARAM_STATUS),
            ppId
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(UserDashboard);