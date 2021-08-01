import { connect } from "react-redux";
import CompanyService from '../../common/CompanyService';
import Pagination from '../../common/Pagination';
import SortColumn from '../../common/SortColumn';
import { Pageable, Sort, combineAsUrlParams } from '../../common/PaginationUtils';
import ShowFilterButton from '../../common/ShowFilterButton';
import { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import CompanyFilter from "./CompanyFilter";
import './Companies.css';
import { useHistory, useLocation } from "react-router-dom";

function Companies(props) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);

    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query));
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'full_name'));
    const [filter, setFilter] = useState(Filter.fromUrlParams(query));
    const [showFilter, setShowFilter] = useState(false);

    useEffect(() => {
        CompanyService.find(filter, pageable, sort)
            .then(response => {
                setPage(response.data);
                history.replace(combineAsUrlParams(filter, pageable, sort));
            });
    }, [pageable, sort, filter, history]);

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
                    Companies
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
            </div>

            {showFilter && <CompanyFilter filter={filter} onChange={onFilterChange} />}
            <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <SortColumn field="full_name" name="Full name" sort={sort} onClick={onSortChange} />
                            <SortColumn field="vatin" name="VATIN" sort={sort} onClick={onSortChange} />
                            <th>Reg. number</th>
                            <th>Status</th>
                            <th>Owner</th>
                            <th>Address</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map((company, index) => <tr key={index}>
                                <td>
                                    <Link to={`/companies/${company.id}`}>
                                        {company.fullName}
                                    </Link>
                                </td>
                                <td>{company.vatin}</td>
                                <td>{company.regNumber}</td>
                                <td>{company.status}</td>
                                <td>Own</td>
                                <td>{company.country}, {company.city}, {company.address}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>
        </div>
    )
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
        if (field === 'status') {
            newFilter[field] = value;
        } else {
            newFilter[field] = value;
        }
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
        return new Filter(
            urlSearchParams.get(Filter.URL_PARAM_NAME_PATTERN) || '',
            urlSearchParams.get(Filter.URL_PARAM_VATIN) || '',
            urlSearchParams.get(Filter.URL_PARAM_COUNTRY) || '',
            urlSearchParams.getAll(Filter.URL_PARAM_STATUS) || ['INACTIVE'],
        );
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps,
    dispatch => ({})
)(Companies);