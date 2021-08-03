import { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import CompanyStatusSelect from '../../common/CompanyStatusSelect';

const ALLOWED_NEXT_STATUSES = {
    'INACTIVE': ['ACTIVE', 'STOPPED'],
    'ACTIVE': ['SUSPENDED', 'STOPPED'],
    'SUSPENDED': ['ACTIVE', 'STOPPED']
}

function ChangeCompanyStatusDialog({ show, status, onClose }) {
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
                <Modal.Title>Company status Change</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-row">
                    <div className="form-group col">
                        <label htmlFor="status">Choose new status</label>
                        <CompanyStatusSelect name="status" optionFilter={optionFilter}
                            onChange={handleStatusChange}
                            selectedStatuses={newStatus}>
                        </CompanyStatusSelect>
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

export default ChangeCompanyStatusDialog;