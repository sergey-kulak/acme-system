import { useCallback, useEffect, useState } from 'react';
import * as Icon from 'react-feather';
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import { FormattedMessage } from 'react-intl';
import Pagination from '../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils';
import { hasRole, ROLE } from "../common/security";
import ShowFilterButton from '../common/ShowFilterButton';
import SortColumn from '../common/SortColumn';
import userService from './userService';
import companyService from '../company/companyService';
import { onError, onSuccess } from '../common/toastNotification';
import UserFilter from './UserFilter';
import UserStatusLabel from './UserStatusLabel';


function UserDashboard({ auth }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);
    const isAdmin = hasRole(auth, ROLE.ADMIN);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'last_name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth));
    const [showFilter, setShowFilter] = useState(true);
    const [companyNames, setCompanyNames] = useState({});

    const loadData = useCallback(() => {
        return userService.find(filter, pageable, sort)
            .then(response => setPage(response.data))
    }, [filter, pageable, sort]);

    useEffect(() => {
        loadData()
            .then(() => {
                let filterUrlParams = filter.toUrlParams(auth);
                history.replace(combineAsUrlParams(filterUrlParams, pageable, sort));
            });
    }, [pageable, sort, filter, history, auth, loadData]);

    useEffect(() => {
        companyService.findNames()
            .then(response => {
                let data = response.data.reduce((acc, item) => {
                    acc[item.id] = item.fullName;
                    return acc;
                }, {})
                setCompanyNames(data);
            });
    }, []);

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
                    className="btn btn-light ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
                <Link to="/users/new" className="btn btn-light ml-2">
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
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(user => <tr key={user.id}>
                                <td className="align-middle">
                                    <UserStatusLabel status={user.status} />
                                </td>
                                <td>
                                    <Link to={`/users/${user.id}`}>
                                        {user.lastName}
                                    </Link>
                                </td>
                                <td>{user.firstName}</td>
                                <td>{user.email}</td>
                                <td>
                                    <FormattedMessage id={`user.role.${user.role.toLowerCase()}`}/>
                                </td>
                                {isAdmin && <td>
                                    <Link to={`/companies/${user.companyId}`}>
                                        {companyNames[user.companyId]}
                                    </Link>
                                </td>}
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

    constructor(companyId, email, role, status) {
        this.companyId = companyId;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.email, this.role, this.status);
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
        return urlData;
    }

    static fromUrlParams(urlSearchParams, auth) {
        let companyId = hasRole(auth, ROLE.ADMIN) ?
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '' :
            auth.user.cmpid;

        return new Filter(
            companyId,
            urlSearchParams.get(Filter.URL_PARAM_EMAIL) || '',
            urlSearchParams.getAll(Filter.URL_PARAM_ROLE),
            urlSearchParams.getAll(Filter.URL_PARAM_STATUS)
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(UserDashboard);