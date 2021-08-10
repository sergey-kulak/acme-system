import { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import * as Icon from 'react-feather';
import { connect } from "react-redux";
import { Link, useHistory } from "react-router-dom";
import { onLogout } from '../../reducers/Auth';
import './Sidebar.css';
import { ROLE, hasRole } from '../../common/security';

function Sidebar({ auth, onLogout }) {
    const [isFixed, setFixed] = useState(true);
    const [isHovered, setHovered] = useState(false);
    let history = useHistory();

    function handleMobileMenuToggle() {
        setFixed(false);
        setHovered(!isHovered)
    }

    function logout(e) {
        e.preventDefault();
        onLogout();
        history.push("/signin");
    }

    const sbClass = isFixed ? '' : isHovered ? 'hovered' : 'collapsed';
    return (
        <div className="px-0">
            <nav id="sidebarMenu"
                className={`d-none d-md-block bg-light sidebar ${sbClass}`}
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}>
                <div className="sidebar-logo">
                    <img className="logo-img" src="/acme-icon.png" alt="logo" />
                    <span className="logo-text mx-2 w-100 text-break">Acme admin</span>
                    <Form.Check type="switch" id="sidebar-switch"
                        checked={isFixed} onChange={() => setFixed(!isFixed)} />
                </div>
                <div className="sidebar-content pt-1 pl-1">
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link to="/" className="nav-link">
                                <Icon.Home className="feather" />
                                <span className="nav-item-text">Home</span>
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to={`/users/${auth.user.id}`} className="nav-link">
                                <Icon.User className="feather" />
                                <span className="nav-item-text">Profile</span>
                            </Link>
                        </li>                         
                        {
                            hasRole(auth, ROLE.ADMIN) && <li className="nav-item">
                                <Link to="/companies" className="nav-link">
                                    <Icon.Box className="feather" />
                                    <span className="nav-item-text">Companies</span>
                                </Link>
                            </li>
                        }
                        {
                            hasRole(auth, ROLE.COMPANY_OWNER) && <li className="nav-item">
                                <Link to="/users" className="nav-link">
                                    <Icon.Users className="feather" />
                                    <span className="nav-item-text">Users</span>
                                </Link>
                            </li>
                        }                       
                        <li className="nav-item">
                            <Link to="/logout" className="nav-link" onClick={logout}>
                                <Icon.LogOut className="feather" />
                                <span className="nav-item-text">Logout</span>
                            </Link>
                        </li>
                    </ul>

                </div>
            </nav>
            <div className={`spacer ${!isFixed && isHovered ? 'hovered' : ''}`}></div>
            <div className="mobile-sidebar d-md-none bg-light">
                <img className="logo-img" src="acme-icon.png" alt="logo" />
                <span className="logo-text ml-2 w-100">Acme admin</span>
                <Button variant="btn-light"
                    onClick={handleMobileMenuToggle}>
                    <Icon.AlignJustify className="feather" />
                </Button>
            </div>
        </div >
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onLogout
})(Sidebar);