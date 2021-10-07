import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Modal from 'react-bootstrap/Modal'
import PublicPointStatusSelect from './PublicPointStatusSelect'

const ALLOWED_NEXT_STATUSES = {
    'INACTIVE': ['ACTIVE', 'STOPPED'],
    'ACTIVE': ['INACTIVE', 'STOPPED']
}

const ALLOWED_ADMIN_NEXT_STATUSES = {
    'ACTIVE': ['SUSPENDED'],
    'SUSPENDED': ['ACTIVE', 'STOPPED']
}

function ChangePublicPointStatusDialog({ show, status, isAdmin, onClose }) {
    const [newStatus, setNewStatus] = useState(status)

    function handleStatusChange(selectedStatus) {
        setNewStatus(selectedStatus)
    }

    function onSave() {
        onClose(newStatus !== status ? newStatus : null)
    }

    function optionFilter(options) {
        let allowedStatuses = ALLOWED_NEXT_STATUSES[status] || []
        if (isAdmin) {
            let adminAllowedStatuses = ALLOWED_ADMIN_NEXT_STATUSES[status] || []
            allowedStatuses = allowedStatuses.concat(adminAllowedStatuses)
        }
        return options.filter(option => allowedStatuses.includes(option.value))
    }

    return (
        <Modal show={show} onHide={() => onClose()} centered animation={false}>
            <Modal.Header closeButton>
                <Modal.Title>Public point status change</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-row">
                    <div className="form-group col">
                        <label htmlFor="status">Choose new status</label>
                        <PublicPointStatusSelect name="status" optionFilter={optionFilter}
                            onChange={handleStatusChange}
                            value={newStatus}>
                        </PublicPointStatusSelect>
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onSave}>
                    Save Changes
                </Button>
                <Button variant="secondary" onClick={e => onClose()}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    )
}

export default ChangePublicPointStatusDialog