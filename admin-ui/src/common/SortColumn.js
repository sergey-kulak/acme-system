import { useState } from 'react';
import * as Icon from 'react-feather';
import './SortColumn.css';

function SortColumn({ field, name, sort, onClick }) {
    const [isAsc, setAsk] = useState(sort.field !== field || sort.direction === 'asc');

    function onSortClick(e) {
        e.preventDefault();
        let sortResult = isAsc;
        if (sort.field === field) {
            sortResult = !sortResult
            setAsk(sortResult);
        }
        onClick({
            field: field,
            direction: sortResult ? 'asc' : 'desc'
        })
    }

    const icon = isAsc ? <Icon.ChevronsUp className="sort-icon" /> : <Icon.ChevronsDown className="sort-icon" />;

    return (
        <th>
            <div className="d-flex align-items-center">
                <a className="font-italic text-body" href="#sort" onClick={onSortClick}>{name}</a>
                {
                    sort.field === field && icon
                }
            </div>
        </th >
    );
}

export default SortColumn;