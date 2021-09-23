import { useEffect } from "react";
import { connect } from "react-redux";
import { BrowserRouter as Router, Route, Switch, Redirect } from "react-router-dom";
import AccessDenied from "./AccessDenied";
import Login from "./Login";
import MainLayout from './main/MainLayout';

function App({ auth }) {

  useEffect(() => {
    if (auth.isAuthenticated) {
      document.title = `${auth.data.publicPointName} menu`;
    }
  }, [auth]);

  return (
    <Router>
      <Switch>
        <Route exact path="/login">
          <Login />
        </Route>
        <Route exact path="/access-denied">
          <AccessDenied />
        </Route>
        {
          auth.isAuthenticated ?
            <Route path="/"><MainLayout /></Route> :
            <Redirect to="/access-denied" />
        }
      </Switch>
    </Router>
  );
}

const mapStateToProps = ({ auth }) => {
  return { auth };
};
export default connect(mapStateToProps,
  dispatch => ({})
)(App);
