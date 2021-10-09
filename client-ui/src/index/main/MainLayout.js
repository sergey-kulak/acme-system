import { useCallback, useEffect } from "react"
import { connect } from "react-redux"
import { Redirect, Route, Switch } from "react-router"
import publicPointNotificationService from "../../common/publicPointNotificationService"
import ToastContainer from '../../common/ToastContainer'
import Menu from "../../menu/Menu"
import MyOrder from "../../order/MyOrder"
import { onSuccess } from "../../common/toastNotification"
import { setOnline } from "../../common/rsocket"
import { onReload } from "../../order/cartReducer"
import Footer from "./Footer"
import './MainLayout.css'
import Sidebar from "./Sidebar"

function MainLayout({ auth, onReload, onSuccess, setOnline }) {

    const processEvent = useCallback((response) => {
        let event = response.data
        switch (event.type) {
            case 'OrderItemStatusChangedEvent':
                onReload()
                break
            case 'OrderStatusChangedEvent':
                switch (event.data.toStatus) {
                    case 'CONFIRMED':
                        onSuccess('Your order was confirmed')
                        onReload()
                        break
                    case 'IN_PROGRESS':
                        onSuccess('Your order is in progress')
                        break
                    case 'READY':
                        onSuccess('Your order is ready')
                        onReload()
                        break
                    case 'DECLINED':
                        onSuccess('Your order was declined')
                        onReload()
                        break
                    case 'DELIVERED':
                    case 'PAID':
                        onReload()
                        break
                    default:
                }
                break
            default:
                console.log(`Unknown type: ${event.type}`)
        }
    }, [onReload, onSuccess])

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
        let subscriber = getSubscriber()
        const request = {
            token: auth.accessToken,
            companyId: auth.user.cmpid,
            publicPointId: auth.user.ppid
        }
        publicPointNotificationService
            .subscribe(request, subscriber)


        return () => {
            publicPointNotificationService.unsubscribe(subscriber)
        }

    }, [auth, getSubscriber])

    return (
        <div className="d-flex flex-column min-vh-100">
            <Sidebar />
            <div className="d-flex flex-grow-1 flex-column">
                <div className="main-content-wrapper">
                    <Switch>
                        <Route exact path="/my-order">
                            <MyOrder />
                        </Route>
                        <Route path="/menu/:categoryId">
                            <Menu />
                        </Route>
                        <Route exact path="/">
                            <Redirect to="/my-order" />
                        </Route>
                    </Switch>
                    <ToastContainer />
                </div>
                <Footer />
            </div>

        </div>
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth }
}
export default connect(mapStateToProps, {
    onReload, onSuccess, setOnline
})(MainLayout)
