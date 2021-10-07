import { useEffect } from "react";
import { IntlProvider } from "react-intl";
import { connect } from "react-redux";
import { BrowserRouter as Router, Redirect, Route, Switch } from "react-router-dom";
import { LOCALES } from '../common/i18n/locales';
import { messages } from '../common/i18n/messages';
import AccessDenied from "./AccessDenied";
import Login from "./Login";
import MainLayout from './main/MainLayout';

function App({ auth }) {
  const locale = LOCALES.ENGLISH

  useEffect(() => {
    if (auth.isAuthenticated) {
      document.title = `${auth.data.publicPointName} menu`;
    }
  }, [auth]);

  return (
    <IntlProvider messages={messages[locale]} locale={locale} defaultLocale={LOCALES.ENGLISH}>
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
    </IntlProvider>
  );
}

const mapStateToProps = ({ auth }) => {
  return { auth };
};
export default connect(mapStateToProps,
  dispatch => ({})
)(App);
