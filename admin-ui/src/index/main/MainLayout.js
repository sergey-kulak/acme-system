import { useCallback, useEffect } from "react"
import { connect } from "react-redux"
import { Route, Switch, useHistory, Redirect } from "react-router-dom"
import SecuredRoute from '../../common/security/SecuredRoute'
import AuthService from "../../common/security/authService"
import { hasRole, ROLE } from "../../common/security"
import { onLogin, onLogout } from '../../common/security/authReducer'
import CompanyDashboard from "../../company/CompanyDashboard"
import CompanyEditor from '../../company/CompanyEditor'
import Home from "../../home/Home"
import UserDashboard from '../../user/UserDashboard'
import UserEditor from '../../user/UserEditor'
import Footer from "./Footer"
import './MainLayout.css'
import Sidebar from "./Sidebar"
import ToastContainer from '../../common/ToastContainer'
import PlanDashboard from "../../plan/PlanDashboad"
import PlanEditor from "../../plan/PlanEditor"
import CompanyViewer from "../../company/CompanyViewer"
import PublicPointDashboard from "../../public-point/PublicPointDashboard"
import PublicPointEditor from "../../public-point/PublicPointEditor"
import PublicPointViewer from "../../public-point/PublicPointViewer"
import PublicPointTableEditor from "../../public-point/PublicPointTableEditor"
import DishDashboard from "../../dish/DishDashboard"
import DishEditor from "../../dish/DishEditor"
import CategoryDashboard from "../../menu/CategoryDashboard"
import CategoryEditor from "../../menu/CategoryEditor"
import LiveOrdersDashboard from "../../order/LiveOrdersDashboard"
import OrderDashboard from "../../order/OrderDashboard"
import OrderViewer from "../../order/OrderViewer"

const UPDATE_TIMEOUT = 120

function MainLayout({ auth, onLogin, onLogout }) {
    const history = useHistory()

    const refreshAccessToken = useCallback(() => {
        console.log("refreshing access token ...")
        AuthService.refreshAccessToken()
            .then(response => {
                onLogin(response.data)
            }, () => {
                onLogout()
                history.push("/signin")
            })
    }, [history, onLogin, onLogout])

    useEffect(() => {
        function checkAndRefresh() {
            let { user } = auth
            let now = new Date().getTime() / 1000
            let needRefresh = (now + UPDATE_TIMEOUT) > user.exp
            if (needRefresh) {
                refreshAccessToken()
            }
        }
        let timerId = setInterval(() => checkAndRefresh(), 60000)
        return () => {
            clearTimeout(timerId)
        }
    }, [auth, refreshAccessToken])

    const isTablesVisible = hasRole(auth, ROLE.ADMIN) || !!auth.user.cmpid

    return (
        <div className="d-flex flex-column flex-md-row min-vh-100">
            <Sidebar />
            <div className="d-flex flex-grow-1 flex-column">
                <div className="main-content-wrapper">
                    <Switch>
                        <Route exact path="/">
                            <Home />
                        </Route>
                        <SecuredRoute exact path="/companies" auth={auth} role={ROLE.ADMIN}>
                            <CompanyDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/companies/:id" auth={auth} role={ROLE.COMPANY_OWNER}>
                            <CompanyEditor />
                        </SecuredRoute>
                        <SecuredRoute path="/company-view/:id" auth={auth} role={ROLE.PP_MANAGER}>
                            <CompanyViewer />
                        </SecuredRoute>
                        <SecuredRoute exact path="/users" auth={auth} role={ROLE.PP_MANAGER}>
                            <UserDashboard />
                        </SecuredRoute>
                        <Route path="/users/:id">
                            <UserEditor />
                        </Route>
                        <SecuredRoute exact path="/plans" auth={auth} role={ROLE.ACCOUNTANT}>
                            <PlanDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/plans/:id" auth={auth} role={ROLE.ACCOUNTANT}>
                            <PlanEditor />
                        </SecuredRoute>
                        <SecuredRoute exact path="/public-points" auth={auth} role={ROLE.COMPANY_OWNER}>
                            <PublicPointDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/public-points/:id" auth={auth} role={ROLE.COMPANY_OWNER}>
                            <PublicPointEditor />
                        </SecuredRoute>
                        <SecuredRoute path="/public-point-view/:id" auth={auth} role={ROLE.PP_MANAGER}>
                            <PublicPointViewer />
                        </SecuredRoute>
                        <Route exact path="/tables" auth={auth} role={ROLE.PP_MANAGER}>
                            {isTablesVisible ? <PublicPointTableEditor /> : <Redirect to="/" />}
                        </Route>
                        <SecuredRoute exact path="/dishes" auth={auth} role={ROLE.COOK}>
                            <DishDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/dishes/:id" auth={auth} role={ROLE.COOK}>
                            <DishEditor />
                        </SecuredRoute>
                        <SecuredRoute exact path="/menu" auth={auth} role={ROLE.COOK}>
                            <CategoryDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/menu/categories/:id" auth={auth} role={ROLE.COOK}>
                            <CategoryEditor />
                        </SecuredRoute>
                        <SecuredRoute path="/live-orders" auth={auth} role={ROLE.COOK}>
                            <LiveOrdersDashboard />
                        </SecuredRoute>
                        <SecuredRoute exact path="/orders" auth={auth} role={ROLE.COOK}>
                            <OrderDashboard />
                        </SecuredRoute>
                        <SecuredRoute path="/orders/:id" auth={auth} role={ROLE.COOK}>
                            <OrderViewer />
                        </SecuredRoute>
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
    onLogin, onLogout
})(MainLayout)