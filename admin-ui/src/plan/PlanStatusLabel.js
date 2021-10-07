import { memo } from 'react'
import './PlanStatusLabel.css'

function PlanStatusLabel({ status }) {
    
    return (
        <div className={`plan-status plan-${status.toLowerCase()}`}/>
    )
}

export default memo(PlanStatusLabel)