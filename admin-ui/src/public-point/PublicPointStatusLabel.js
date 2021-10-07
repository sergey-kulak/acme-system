import { memo } from 'react'
import { FormattedMessage } from 'react-intl'
import './PublicPointStatusLabel.css'

function PublicPointStatusLabel({ status, showText }) {

    return (
        <div className="d-flex align-items-center">
            <div className={`pp-status pp-${status.toLowerCase()}`} />
            {showText && <div className="ml-2 cmp-label-text">
                <FormattedMessage id={`company.status.${status.toLowerCase()}`} />
            </div>}
        </div>
    )
}

export default memo(PublicPointStatusLabel)