import { connect } from "react-redux";
import CompanyService from '../../common/CompanyService';
import Pagination from '../../common/Pagination';
import SortColumn from '../../common/SortColumn';
import ShowFilterButton from '../../common/ShowFilterButton';
import { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import CompanyFilter from "./CompanyFilter";
import './Companies.css';

function Companies(props) {
    const [page, setPage] = useState({ content: [] });
    const [pageable, setPageable] = useState({ page: 1, size: 5 });
    const [sort, setSort] = useState({ field: 'full_name', direction: 'asc' });
    const [filter, setFilter] = useState({
        namePattern: '',
        vatin: '',
        country: '',
        status: 'INACTIVE'
    });
    const [showFilter, setShowFilter] = useState(false);

    useEffect(() => {
        CompanyService.find(filter, pageable, sort)
            .then(response => setPage(response.data));
    }, [pageable, sort, filter]);

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

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps,
    dispatch => ({})
)(Companies);