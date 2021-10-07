import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Modal from 'react-bootstrap/Modal'


function CommentDialog({ show, comment, onClose }) {
    const [newComment, setNewStatus] = useState(comment)

    function handleChange(e) {
        setNewStatus(e.target.value)
    }

    function onSave() {
        onClose(newComment)
    }

    function onHide() {
        onClose(comment)
    }

    function onClear() {
        onClose(null)
    }

    return (
        <Modal show={show} onHide={onHide} centered animation={false}>
            <Modal.Body>
                <div className="form-row">
                    <div className="form-group col mb-0">
                        <label>Leave a comment:</label>
                        <textarea rows={5} value={newComment} onChange={handleChange}
                            className="form-control" />
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onSave}>
                    Save
                </Button>
                <Button variant="secondary" onClick={onHide}>
                    Close
                </Button>
                <Button variant="secondary" onClick={onClear}>
                    Clear
                </Button>
            </Modal.Footer>
        </Modal>
    )
}

export default CommentDialog