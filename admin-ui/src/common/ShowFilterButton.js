import * as Icon from 'react-feather';
import Tooltip from 'react-bootstrap/Tooltip';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import * as Utils from './Utils';


function ShowFilterButton({ filter, showFilter, className, onClick }) {

    const emptyFilter = Utils.isEmptyObject(filter);

    return (
        <OverlayTrigger
            key="right"
            placement="right"
            delay={100}
            overlay={
                <Tooltip id="filter-button-tooltip">
                    {showFilter ? 'Hide' : 'Show'} filters
                </Tooltip>
            }>
            <button type="button" className={className}
                onClick={onClick}>
                <Icon.Filter className="filter-icon" />
                {emptyFilter && <span class="badge badge-pill badge-warning">!</span>}
            </button>
        </OverlayTrigger>
    );
}

export default ShowFilterButton;