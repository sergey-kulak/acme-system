import { memo } from 'react';
import { FormattedMessage } from 'react-intl';
import './CompanyStatusLabel.css';

function CompanyStatusLabel({ status, showText }) {

    return (
        <div className="d-flex align-items-center">
            <div className={`cmp-status cmp-${status.toLowerCase()}`} />
            {showText && <div className="ml-2 cmp-label-text">
                <FormattedMessage id={`company.status.${status.toLowerCase()}`} />
            </div>}
        </div>
    );
}

export default memo(CompanyStatusLabel);