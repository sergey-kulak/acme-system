import { Redirect, Route, Switch } from "react-router";
import Menu from "../../menu/Menu";
import Sidebar from "./Sidebar";
import Footer from "./Footer";
import './MainLayout.css';
import MyOrder from "../../order/MyOrder";
import ToastContainer from '../../common/ToastContainer'

function MainLayout() {

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
                            <Menu/>
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

export default MainLayout
