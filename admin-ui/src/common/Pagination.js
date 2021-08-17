import BsPagination from 'react-bootstrap/Pagination';
import './Pagination.css';
import { Pageable } from './paginationUtils';

const VISIBLE_PAGES = 5;

const calculatePages = (currentPage, totalPages) => {
    let pos = currentPage - Math.floor(VISIBLE_PAGES / 2);
    if (pos < 1) {
        pos = 1;
    }
    let pages = [];
    while (pages.length < VISIBLE_PAGES && pos <= totalPages) {
        pages.push(pos++);
    }
    if (pages.length < VISIBLE_PAGES) {
        pos = pages[0] - 1;
        while (pages.length < VISIBLE_PAGES && pos >= 1) {
            pages.unshift(pos--);
        }
    }
    return pages;
}

function Pagination({ page, onPageableChange, className }) {

    function onPageClick(pageIndex) {
        onPageableChange(new Pageable(pageIndex, page.size));
    }

    function onSizeClick(event) {
        onPageableChange(new Pageable(page.number, event.target.value));
    }


    if (!page || typeof page.number !== 'number') {
        return "";
    }
    const currentPage = page.number + 1;
    const totalPages = page.totalPages;
    const pageIndexes = calculatePages(currentPage, totalPages)
    const totalElements = page.totalElements;
    const size = page.size;

    return (
        <div className={`pagination-wrapper row ${className}`}>
            <div className="col-md-6 d-flex align-items-center">
                <BsPagination className="mb-3">
                    <BsPagination.First
                        onClick={e => onPageClick(1)} disabled={currentPage === 1} />
                    <BsPagination.Prev
                        onClick={e => onPageClick(currentPage - 1)} disabled={currentPage === 1} />
                    {
                        pageIndexes.map(pageIndex =>
                            <BsPagination.Item key={pageIndex}
                                onClick={e => onPageClick(pageIndex)}
                                active={pageIndex === currentPage}>
                                {pageIndex}
                            </BsPagination.Item>
                        )
                    }
                    <BsPagination.Next
                        onClick={e => onPageClick(currentPage + 1)} disabled={currentPage === totalPages} />
                    <BsPagination.Last
                        onClick={e => onPageClick(totalPages)} disabled={currentPage === totalPages} />
                </BsPagination>
            </div>
            <div className="col-md-6 d-flex align-items-center">
                <span className="pagination-descr mb-3">
                    Found {totalElements} {'item' + (totalElements > 1 ? 's' : '')}, per page:
                </span>
                <select className="form-control ml-2 mb-3" style={{ width: '4rem' }}
                    value={size} onChange={onSizeClick}>
                    <option>5</option>
                    <option>10</option>
                    <option>25</option>
                    <option>50</option>
                </select>
            </div>
        </div>
    );
}

export default Pagination;