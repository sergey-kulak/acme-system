import { useCallback, useEffect } from "react";
import { connect } from "react-redux";
import { Route, Switch, useHistory } from "react-router-dom";
import AuthRoute from '../../common/security/AuthRoute';
import AuthService from "../../common/security/authService";
import { ROLE } from "../../common/security";
import { onLogin, onLogout } from '../../common/security/authReducer';
import CompanyDashboard from "../../company/CompanyDashboard";
import CompanyEditor from '../../company/CompanyEditor';
import Home from "../../home/Home";
import UserDashboard from '../../user/UserDashboard';
import UserEditor from '../../user/UserEditor';
import Footer from "./Footer";
import './MainLayout.css';
import Sidebar from "./Sidebar";
import ToastContainer from './ToastContainer';


const UPDATE_TIMEOUT = 120;

function MainLayout({ auth, onLogin, onLogout }) {
    const history = useHistory();

    const refreshAccessToken = useCallback(() => {
        console.log("refreshing access token ...");
        AuthService.refreshAccessToken()
            .then(response => {
                onLogin(response.data);
            }, () => {
                onLogout();
                history.push("/signin");
            });
    }, [history, onLogin, onLogout]);

    useEffect(() => {
        function checkAndRefresh() {
            let { user } = auth;
            let now = new Date().getTime() / 1000;
            let needRefresh = (now + UPDATE_TIMEOUT) > user.exp
            if (needRefresh) {
                refreshAccessToken();
            }
        }
        let timerId = setInterval(() => checkAndRefresh(), 60000);
        return () => {
            clearTimeout(timerId);
        }
    }, [auth, refreshAccessToken]);



    return (
        <div className="d-flex flex-column flex-md-row min-vh-100">
            <Sidebar />
            <div className="d-flex flex-grow-1 flex-column">
                <div className="main-content-wrapper">
                    <Switch>
                        <Route exact path="/">
                            <Home />
                        </Route>
                        <AuthRoute exact path="/companies" auth={auth} role={ROLE.ADMIN}>
                            <CompanyDashboard />
                        </AuthRoute>
                        <AuthRoute path="/companies/:id" auth={auth} role={ROLE.COMPANY_OWNER}>
                            <CompanyEditor />
                        </AuthRoute>
                        <AuthRoute exact path="/users" auth={auth} role={ROLE.COMPANY_OWNER}>
                            <UserDashboard />
                        </AuthRoute>
                        <Route path="/users/:id">
                            <UserEditor />
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
    return { auth };
};
export default connect(mapStateToProps, {
    onLogin, onLogout
})(MainLayout);