import { useEffect, useState } from 'react'
import Button from 'react-bootstrap/Button'
import * as Icon from 'react-feather'
import { connect } from 'react-redux'
import { Link } from "react-router-dom"
import './Sidebar.css'
import menuService from '../../menu/menuService'
import { onSuccess } from "../../common/toastNotification"

function Sidebar({ auth, cart, rsocket, onSuccess }) {
    const [isOpened, setIsOpened] = useState(false)
    const [categories, setCategories] = useState([])

    useEffect(() => {
        menuService.getCategories()
            .then(response => response.data)
            .then(setCategories)
    }, [])

    function handleMobileMenuToggle() {
        setIsOpened(!isOpened)
    }

    function doNothing(e) {
        e.preventDefault()
    }

    function hide() {
        setIsOpened(false)
    }

    function hadleCallWaiterClick() {
        menuService.callWaiter()
            .then(() => onSuccess('Waiters have been informed and will come to you very soon'))
    }

    const sbClass = isOpened ? 'opened' : ''
    const cartTotal = cart.items.reduce((acc, item) => acc + item.quantity, 0)
    const rsocketClass = rsocket.isOnline ? 'badge-success online' : 'badge-danger offline'

    return (
        <div className="px-0">
            <div className="mobile-sidebar bg-light">
                <img className="logo-img" src="/acme-icon.png" alt="logo" />
                <span className={`badge badge-pill rsocket-status ${rsocketClass}`}>.</span>
                <span className="logo-text ml-2">
                    {auth.data.publicPointName}
                </span>
                <div className="flex-grow-1 d-flex justify-content-center">
                    <button className="btn btn-light" onClick={hadleCallWaiterClick}>
                        <Icon.Bell className="feather mr-1" />
                        Call waiter
                    </button>
                </div>
                <span className="">
                </span>
                <Button variant="btn btn-light position-relative" onClick={handleMobileMenuToggle}>
                    {cartTotal > 0 &&
                        <span className="badge badge-pill badge-primary ml-2 cart-badge ">
                            {cartTotal}
                        </span>}
                    <Icon.AlignJustify className="feather" />
                </Button>
            </div>
            <nav id="sidebarMenu"
                className={`d-none bg-light sidebar ${sbClass}`}>
                <div className="sidebar-content pt-1 pl-1">
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link to="/my-order" className="nav-link" onClick={hide}>
                                <Icon.ShoppingCart className="feather" />
                                <span className="nav-item-text">My order</span>
                                {cartTotal > 0 &&
                                    <span className="badge badge-pill badge-primary ml-2">
                                        {cartTotal}
                                    </span>}
                            </Link>
                        </li>
                        {categories.length > 0 && <>
                            <li className="nav-item">
                                <a href="#menu" onClick={doNothing} className="nav-link">
                                    <Icon.BookOpen className="feather" />
                                    <span className="nav-item-text">Menu</span>
                                </a>
                            </li>
                            {
                                categories.map(ctg => <li className="nav-item" key={ctg.id}>
                                    <Link
                                        to={{
                                            pathname: `/menu/${ctg.id}`,
                                            state: { categoryName: ctg.name }
                                        }}
                                        className="nav-link"
                                        onClick={hide}>
                                        <Icon.ChevronRight className="feather category-icon" />
                                        <span className="nav-item-text">{ctg.name}</span>
                                    </Link>
                                </li>)
                            }
                        </>}
                    </ul>

                </div>
            </nav>

        </div >
    )
}

const mapStateToProps = ({ auth, cart, rsocket }) => ({ auth, cart, rsocket })

export default connect(mapStateToProps, {
    onSuccess
})(Sidebar)