import Toast from 'react-bootstrap/Toast';
import * as Icon from 'react-feather';
import { connect } from "react-redux";
import { clearMessage } from './toastNotification';

const DEFAULT_OPTIONS = {
    autohide: true,
    delay: 3000
}

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
    const options = { ...DEFAULT_OPTIONS, ...((toast && toast.options) || {}) }

    return (
        hasMessage && <Toast onClose={clearMessage} show={true} className="rounded"
            delay={options.delay} autohide={options.autohide}
            style={{
                position: 'fixed',
                bottom: '1.5rem',
                right: '1.5rem',
            }}>
            <Toast.Body className={'rounded text-break ' + getToastStyle()} >
                <div className="d-flex">
                    <div>
                        <span className="toast-message">{toast.message}</span>
                    </div>
                    {!options.autohide && <div>
                        <a href="#close" onClick={clearMessage}>
                            <Icon.XCircle className="filter-icon" />
                        </a>
                    </div>}
                </div>
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