import { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import PlanCard from './PlanCard';
import planService from './planService';


function ChoosePlanDialog({ show, company, onClose }) {
    const [plan, setPlan] = useState();
    const [plans, setPlans] = useState([]);

    useEffect(() => {
        planService.findActive(company.country)
            .then(response => setPlans(response.data
                .sort((p1, p2) => p1.maxTableCount - p2.maxTableCount)));
    }, [company.country]);

    function onChoose() {
        onClose(plan && plan.id);
    }

    function getCardClassName(cardPlan) {
        return plan && plan.id === cardPlan.id ? 'border-primary selected' : '';
    }

    function onCardClick(newPlan) {
        setPlan(newPlan);
    }

    return (
        <Modal show={show} onHide={() => onClose()} centered animation={false}>
            <Modal.Header closeButton>
                <Modal.Title>Plan change</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-row">
                    <div className="form-group col">
                        <label htmlFor="status">Choose new plan</label>
                        <div className="row row-cols-1">
                            {
                                plans.map(planItem =>
                                    <div className="col mb-4" key={planItem.id}
                                        onClick={e => onCardClick(planItem)}>
                                        <PlanCard plan={planItem} className={getCardClassName(planItem)} />
                                    </div>
                                )
                            }
                        </div>
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onChoose}>
                    Choose
                </Button>
                <Button variant="secondary" onClick={e => onClose()}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default ChoosePlanDialog;