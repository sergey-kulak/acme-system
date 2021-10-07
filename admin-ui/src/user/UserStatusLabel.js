import { memo } from 'react'
import './UserStatusLabel.css'

function UserStatusLabel({ status }) {

    return (
        <div className={`usr-status usr-${status.toLowerCase()}`}/>
    )
}

export default memo(UserStatusLabel)