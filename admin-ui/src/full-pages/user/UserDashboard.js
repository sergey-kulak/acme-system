import { useCallback, useEffect, useState } from 'react';
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import Pagination from '../../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../../common/PaginationUtils';
import ShowFilterButton from '../../common/ShowFilterButton';
import SortColumn from '../../common/SortColumn';
import UserService from '../../common/UserService';
import { onError, onSuccess } from '../../reducers/ToastNotification';
import UserFitler from './UserFilter';
import * as Icon from 'react-feather';

function UserDashboard() {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'last_name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query));
    const [showFilter, setShowFilter] = useState(true);

    const loadData = useCallback(() => {
        return UserService.find(filter, pageable, sort)
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

            {showFilter && <UserFitler filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <SortColumn field="last_name" name="Last name" sort={sort} onClick={onSortChange} />
                            <SortColumn field="first_name" name="First name" sort={sort} onClick={onSortChange} />
                            <th>Email</th>
                            <th>Status</th>
                            <th>Role</th>
                            <th>Company</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map((user, index) => <tr key={index}>
                                <td>
                                    <Link to={`/users/${user.id}`}>
                                        {user.lastName}
                                    </Link>
                                </td>
                                <td>{user.firstName}</td>
                                <td>{user.email}</td>
                                <td>{user.status}</td>
                                <td>{user.role}</td>
                                <td>
                                    <Link to={`/companies/${user.companyId}`}>
                                        {user.companyId}
                                    </Link>
                                </td>
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
        let newFilter = new Filter(this.companyId, this.email);
        newFilter[field] = value;
        return newFilter;
    }

    toUrlParams() {
        return {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_EMAIL]: this.email,
            [Filter.URL_PARAM_ROLE]: this.role,
            [Filter.URL_PARAM_STATUS]: this.status,
        }
    }

    static fromUrlParams(urlSearchParams) {
        return new Filter(
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '',
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