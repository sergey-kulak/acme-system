import { useCallback, useEffect, useState } from 'react'
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import * as Icon from 'react-feather'
import { connect } from "react-redux"
import { Link, useHistory } from "react-router-dom"
import { hasRole, ROLE } from '../../common/security'
import { onSuccess } from '../../common/toastNotification'
import { setOnline } from '../../common/rsocket'
import { onLogout } from '../../common/security/authReducer'
import publicPointNotificationService from '../../public-point/publicPointNotificationService'
import './Sidebar.css'

function Sidebar({ auth, onLogout, rsocket, setOnline, onSuccess }) {
    const [isFixed, setFixed] = useState(true)
    const [isHovered, setHovered] = useState(false)
    let history = useHistory()

    const processEvent = useCallback((response) => {
        let event = response.data
        if (event.type === 'CallWaiterEvent') {
            onSuccess(event.data.message, { autohide: false })
        }
    }, [onSuccess])

    const getSubscriber = useCallback(() => {
        return {
            onComplete: () => setOnline(false),
            onError: error => {
                if ('RSocket: The connection was closed.' !== error.message) {
                    console.error(error)
                }
                setOnline(false)
            },
            onNext: processEvent,
            onSubscribe: () => {
                setOnline(true)
            }
        }
    }, [processEvent, setOnline])

    useEffect(() => {
        let subscriber
        if (auth.user.cmpid && auth.user.ppid) {
            subscriber = getSubscriber()
            const request = {
                token: auth.accessToken,
                companyId: auth.user.cmpid,
                publicPointId: auth.user.ppid
            }
            publicPointNotificationService
                .subscribe(request, subscriber)
        }

        return () => {
            if (subscriber) {
                publicPointNotificationService.unsubscribe(subscriber)
            }
        }
    }, [auth, getSubscriber])

    function handleMobileMenuToggle() {
        setFixed(false)
        setHovered(!isHovered)
    }

    function logout(e) {
        e.preventDefault()
        onLogout()
        history.push("/signin")
    }

    function handleLinkClick(e) {
        if (window.document.body.clientWidth <= 767) {
            handleMobileMenuToggle()
        }
    }

    function calcMenuHeight() {
        const bodyHeight = window.document.body.scrollHeight
        const topPos = window.document.body.clientWidth > 767 ? 0 : 3.5
        const customHeight = `calc(${bodyHeight}px - ${topPos}rem`
        if (window.document.body.clientWidth > 767) {
            return isFixed || !isHovered ? '100%' : customHeight
        } else {
            return bodyHeight < 500 ? 'auto' : customHeight
        }
    }

    const sbClass = isFixed ? '' : isHovered ? 'hovered' : 'collapsed'
    const cssHeight = calcMenuHeight()

    const isCompanyUser = hasRole(auth, ROLE.ADMIN) || !!auth.user.cmpid
    const rsocketClass = rsocket.isOnline ? 'badge-success online' : 'badge-danger offline'

    return (
        <div className="px-0 position-relative">
            <div className="mobile-sidebar d-md-none bg-light">
                <img className="logo-img" src="/acme-icon.png" alt="logo" />
                <span className={`badge badge-pill rsocket-status ${rsocketClass} rsocket-status-mobile`}>.</span>
                <span className="logo-text ml-2 w-100">Acme admin</span>
                <Button variant="btn-light"
                    onClick={handleMobileMenuToggle}>
                    <Icon.AlignJustify className="feather" />
                </Button>
            </div>
            <nav id="sidebarMenu"
                className={`d-none d-md-block bg-light sidebar ${sbClass}`}
                style={{ height: cssHeight }}
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}>
                <div className="sidebar-logo">
                    <img className="logo-img" src="/acme-icon.png" alt="logo" />
                    <span className={`badge badge-pill rsocket-status ${rsocketClass}`}>.</span>
                    <span className="logo-text mx-2 w-100 text-break">Acme admin</span>
                    <Form.Check type="switch" id="sidebar-switch"
                        checked={isFixed} onChange={() => setFixed(!isFixed)} />
                </div>
                <div className="sidebar-content pt-1 pl-1">
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link to="/" className="nav-link" onClick={handleLinkClick}>
                                <Icon.Home className="feather" />
                                <span className="nav-item-text">Home</span>
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to={`/users/${auth.user.id}`} className="nav-link"
                                onClick={handleLinkClick}>
                                <Icon.User className="feather" />
                                <span className="nav-item-text">Profile</span>
                            </Link>
                        </li>
                        {
                            auth.user.cmpid && hasRole(auth, ROLE.PP_MANAGER) &&
                            <li className="nav-item">
                                <Link to={`/company-view/current`} className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Box className="feather" />
                                    <span className="nav-item-text">Company</span>
                                </Link>
                            </li>
                        }
                        {
                            hasRole(auth, ROLE.ACCOUNTANT) && <li className="nav-item">
                                <Link to="/plans" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Pocket className="feather" />
                                    <span className="nav-item-text">Plans</span>
                                </Link>
                            </li>
                        }
                        {
                            hasRole(auth, ROLE.ADMIN) && <li className="nav-item">
                                <Link to="/companies" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Package className="feather" />
                                    <span className="nav-item-text">Companies</span>
                                </Link>
                            </li>
                        }
                        {
                            hasRole(auth, ROLE.COMPANY_OWNER) && <li className="nav-item">
                                <Link to="/public-points" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.MapPin className="feather" />
                                    <span className="nav-item-text">Public points</span>
                                </Link>
                            </li>
                        }
                        {
                            hasRole(auth, ROLE.PP_MANAGER) && <li className="nav-item">
                                <Link to="/users" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Users className="feather" />
                                    <span className="nav-item-text">Users</span>
                                </Link>
                            </li>
                        }
                        {
                            isCompanyUser && <li className="nav-item">
                                <Link to="/tables" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Grid className="feather" />
                                    <span className="nav-item-text">Tables</span>
                                </Link>
                            </li>
                        }
                        {
                            isCompanyUser && <li className="nav-item">
                                <Link to="/dishes" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Coffee className="feather" />
                                    <span className="nav-item-text">Dishes</span>
                                </Link>
                            </li>
                        }
                        {
                            isCompanyUser && <li className="nav-item">
                                <Link to="/menu" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.BookOpen className="feather" />
                                    <span className="nav-item-text">Menu</span>
                                </Link>
                            </li>
                        }
                        {
                            isCompanyUser && <li className="nav-item">
                                <Link to="/live-orders" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Activity className="feather" />
                                    <span className="nav-item-text">Live orders</span>
                                </Link>
                            </li>
                        }
                        {
                            isCompanyUser && <li className="nav-item">
                                <Link to="/orders" className="nav-link"
                                    onClick={handleLinkClick}>
                                    <Icon.Archive className="feather" />
                                    <span className="nav-item-text">Orders</span>
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
            <div className={`spacer ${!isFixed && isHovered ? 'hovered' : ''}`}
                style={{ height: cssHeight }}
            />
        </div >
    )
}

const mapStateToProps = ({ auth, rsocket }) => ({ auth, rsocket })

export default connect(mapStateToProps, {
    onLogout, setOnline, onSuccess
})(Sidebar)