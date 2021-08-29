import { useEffect, useState, useCallback } from 'react';
import { connect } from "react-redux";
import { Link, useHistory, useLocation } from "react-router-dom";
import companyService from './companyService';
import Pagination from '../common/Pagination';
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils';
import ShowFilterButton from '../common/ShowFilterButton';
import SortColumn from '../common/SortColumn';
import ChangeCompanyStatusDialog from './ChangeCompanyStatusDialog';
import './CompanyDashboard.css';
import CompanyFilter from "./CompanyFilter";
import { onSuccess, onError } from '../common/toastNotification'
import CompanyStatusLabel from './CompanyStatusLabel';
import { getErrorMessage } from '../common/utils';

function CompanyDashboard({ onSuccess, onError }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'full_name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query));
    const [showFilter, setShowFilter] = useState(false);
    const [showStatusChangeDialog, setShowStatusChangeDialog] = useState(false);
    const [modifiedCompany, setModifiedCompany] = useState();

    const loadData = useCallback(() => {
        return companyService.find(filter, pageable, sort)
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

    function onCompanyStatusClick(e, modifiedCompany) {
        e.preventDefault();
        setModifiedCompany(modifiedCompany);
        setShowStatusChangeDialog(true);
    }

    function onStatusChange(newStatus) {
        setShowStatusChangeDialog(false);
        if (newStatus) {
            companyService.changeStatus(modifiedCompany.id, { status: newStatus })
                .then(() => {
                    onSuccess("Company status was changed successfully");
                    loadData();
                }, error => {
                    onError(getErrorMessage(error.response.data));
                });
        }
    }
    
    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Companies
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
            </div>

            {showFilter && <CompanyFilter filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th className="status-th" />
                            <SortColumn field="full_name" name="Full name" sort={sort} onClick={onSortChange} />
                            <SortColumn field="vatin" name="VATIN" sort={sort} onClick={onSortChange} />
                            <th>Reg. number</th>
                            <th>Email</th>
                            <th>Address</th>
                            <th>Phone</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(company => <tr key={company.id}>
                                <td className="align-middle">
                                    {company.status === 'STOPPED' ?
                                        <CompanyStatusLabel status={company.status} /> :
                                        <a href="#status" onClick={e => onCompanyStatusClick(e, company)}>
                                            <CompanyStatusLabel status={company.status} />
                                        </a>
                                    }
                                </td>
                                <td>
                                    <Link to={`/company-view/${company.id}`}>
                                        {company.fullName}
                                    </Link>
                                </td>
                                <td>{company.vatin}</td>
                                <td>{company.regNumber}</td>
                                <td>{company.email}</td>
                                <td>{company.country}, {company.city}, {company.address}</td>
                                <td>{company.phone}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>
            <div>
                {showStatusChangeDialog
                    && <ChangeCompanyStatusDialog show={showStatusChangeDialog}
                        status={modifiedCompany.status} onClose={onStatusChange}
                    />
                }
            </div>
        </div>
    );
}

class Filter {
    static URL_PARAM_NAME_PATTERN = 'pn';
    static URL_PARAM_VATIN = 'vi';
    static URL_PARAM_COUNTRY = 'cn';
    static URL_PARAM_STATUS = 'st';

    constructor(namePattern, vatin, country, status) {
        this.namePattern = namePattern;
        this.vatin = vatin;
        this.country = country;
        this.status = status;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.namePattern, this.vatin,
            this.country, this.status);
        newFilter[field] = value;
        return newFilter;
    }

    toUrlParams() {
        return {
            [Filter.URL_PARAM_NAME_PATTERN]: this.namePattern,
            [Filter.URL_PARAM_VATIN]: this.vatin,
            [Filter.URL_PARAM_COUNTRY]: this.country,
            [Filter.URL_PARAM_STATUS]: this.status,
        }
    }

    static fromUrlParams(urlSearchParams) {
        let urlStatuses = urlSearchParams.getAll(Filter.URL_PARAM_STATUS);
        return new Filter(
            urlSearchParams.get(Filter.URL_PARAM_NAME_PATTERN) || '',
            urlSearchParams.get(Filter.URL_PARAM_VATIN) || '',
            urlSearchParams.get(Filter.URL_PARAM_COUNTRY) || '',
            urlStatuses && urlStatuses.length ? urlStatuses : ['INACTIVE'],
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(CompanyDashboard);