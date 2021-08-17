import { IntlProvider } from 'react-intl';
import { connect } from "react-redux";
import { BrowserRouter as Router, Redirect, Route, Switch } from "react-router-dom";
import { LOCALES } from '../common/i18n/locales';
import { messages } from '../common/i18n/messages';
import MainLayout from './main/MainLayout';
import SignIn from './signin/SignIn';
import SignUp from './signup/SignUp';
import Welcome from './welcome/Welcome';

function App({ auth }) {
  const locale = LOCALES.ENGLISH

  return (
    <IntlProvider messages={messages[locale]} locale={locale} defaultLocale={LOCALES.ENGLISH}>
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
    </IntlProvider>
  );
}


const mapStateToProps = ({ auth }) => {
  return { auth };
};
export default connect(mapStateToProps,
  dispatch => ({})
)(App);
