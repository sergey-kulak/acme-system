import { memo } from 'react';
import './CompanyStatusLabel.css';

function CompanyStatusLabel({ status }) {
    
    return (
        <div className={`cmp-status cmp-${status.toLowerCase()}`}/>
    );
}

export default memo(CompanyStatusLabel);