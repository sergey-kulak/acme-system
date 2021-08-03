import './MainLayout.css';

import Sidebar from "./Sidebar";
import Home from "../home/Home";
import CompanyDashboard from "../company/CompanyDashboard";
import Footer from "./Footer";
import { Switch, Route } from "react-router-dom";
import ToastContainer from './ToastContainer';
import CompanyEditor from '../company/CompanyEditor';

function MainLayout() {
    return (
        <div className="d-flex flex-column flex-md-row min-vh-100">
            <Sidebar />
            <div className="d-flex flex-grow-1 flex-column">
                <div className="main-content-wrapper">
                    <Switch>
                        <Route exact path="/">
                            <Home />
                        </Route>
                        <Route exact path="/companies">
                            <CompanyDashboard />
                        </Route>
                        <Route path="/companies/:id">
                            <CompanyEditor />
                        </Route>
                    </Switch>
                    <ToastContainer/>
                </div>
                <Footer />
            </div>

        </div>
    )
}

export default MainLayout;