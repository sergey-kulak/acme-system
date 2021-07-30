import './App.css';

import SignIn from './partial-pages/signin/SignIn';
import SignUp from './partial-pages/signup/SignUp';
import Welcome from './partial-pages/welcome/Welcome';
import MainLayout from './full-pages/main/MainLayout'

import { connect } from "react-redux";
import { BrowserRouter as Router, Switch, Route, Redirect } from "react-router-dom";


function App(props) {

  const auth = props.auth;
    
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
              <Route path="/"><MainLayout/></Route> :
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
