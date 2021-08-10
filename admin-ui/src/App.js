import { connect } from "react-redux";
import { BrowserRouter as Router, Redirect, Route, Switch } from "react-router-dom";
import './App.css';
import MainLayout from './full-pages/main/MainLayout';
import SignIn from './partial-pages/signin/SignIn';
import SignUp from './partial-pages/signup/SignUp';
import Welcome from './partial-pages/welcome/Welcome';



function App({auth}) {

  return (
    <Router>
      <Switch>
        <Route path="/welcome">
          <Welcome />
        </Route>
        <Route path="/signin">
          <SignIn />
        </Route>
        <Route path="/signup">
          <SignUp />
        </Route>
        {
          auth.isAuthenticated ?
            <Route path="/"><MainLayout /></Route> :
            <Redirect to="/welcome" />
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
