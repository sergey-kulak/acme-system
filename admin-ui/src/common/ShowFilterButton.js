import * as Icon from 'react-feather';
import * as Utils from './utils';


function ShowFilterButton({ filter, showFilter, className, onClick }) {

    const emptyFilter = Utils.isEmptyObject(filter);

    return (
        <button type="button" className={className}
            onClick={onClick}>
            <Icon.Filter className="filter-icon" />
            {emptyFilter && <span className="badge badge-pill badge-warning">!</span>}
        </button>
    );
}

export default ShowFilterButton;