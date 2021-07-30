import './MainLayout.css';

import { connect } from "react-redux";
import Sidebar from "./Sidebar";
import Home from "../home/Home";
import Companies from "../company/Companies";
import Footer from "./Footer";
import { Switch, Route } from "react-router-dom";

function MainLayout(props) {
    return (
        <div className="d-flex flex-column flex-md-row min-vh-100">
            <Sidebar />
            <div className="d-flex flex-grow-1 flex-column">
                <div className="main-content-wrapper">
                    <Switch>
                        <Route exact path="/">
                            <Home />
                        </Route>
                        <Route path="/companies">
                            <Companies />
                        </Route>
                    </Switch>
                </div>
                <Footer/>
            </div>
            
        </div>
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps,
    dispatch => ({})
)(MainLayout);