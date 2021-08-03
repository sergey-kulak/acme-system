import Toast from 'react-bootstrap/Toast';
import { clearMessage } from '../../reducers/ToastNotification';
import { connect } from "react-redux";

function ToastContainer({ toast, clearMessage }) {
    function getToastStyle() {
        switch (toast.type) {
            case 'error':
                return 'bg-danger';
            default:
                return 'bg-success';
        }
    }

    const hasMessage = !!toast.message
    return (
        hasMessage && <Toast onClose={clearMessage} show={true} className="rounded"
            delay={3000} autohide
            style={{
                position: 'fixed',
                bottom: '1.5rem',
                right: '1.5rem',
            }}>
            <Toast.Body className={'rounded ' + getToastStyle()}>
                <span className="toast-message">{toast.message}</span>
            </Toast.Body>
        </Toast>

    );
}

const mapStateToProps = ({ toast }) => {
    return { toast };
};

export default connect(mapStateToProps, {
    clearMessage
})(ToastContainer);