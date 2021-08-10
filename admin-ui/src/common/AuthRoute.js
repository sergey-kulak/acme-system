import { Route, Redirect } from "react-router-dom";
import { hasRole } from './security';



function AuthRoute({ auth, role, children, ...props }) {
    return (
        <Route {...props}>
            {hasRole(auth, role) ? children : <Redirect to="/" />}
        </Route >

    );
}

export default AuthRoute;