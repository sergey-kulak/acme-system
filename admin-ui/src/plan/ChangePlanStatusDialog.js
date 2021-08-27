import { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import PlanStatusSelect from './PlanStatusSelect';

const ALLOWED_NEXT_STATUSES = {
    'INACTIVE': ['ACTIVE', 'STOPPED'],
    'ACTIVE': ['STOPPED']
}

function ChangePlanStatusDialog({ show, status, onClose }) {
    const [newStatus, setNewStatus] = useState(status);

    function handleStatusChange(selectedStatus) {
        setNewStatus(selectedStatus);
    }

    function onSave() {
        onClose(newStatus !== status ? newStatus : null);
    }

    function optionFilter(options) {
        let allowedStatuses = ALLOWED_NEXT_STATUSES[status];
        return options.filter(option => allowedStatuses.includes(option.value));
    }

    return (
        <Modal show={show} onHide={() => onClose()} centered animation={false}>
            <Modal.Header closeButton>
                <Modal.Title>PLan status change</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-row">
                    <div className="form-group col">
                        <label htmlFor="status">Choose new status</label>
                        <PlanStatusSelect name="status" optionFilter={optionFilter}
                            onChange={handleStatusChange}
                            value={newStatus}>
                        </PlanStatusSelect>
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
    );
}

export default ChangePlanStatusDialog;