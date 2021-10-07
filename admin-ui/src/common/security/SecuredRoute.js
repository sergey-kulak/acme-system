import { Route, Redirect } from "react-router-dom"
import { hasRole } from './index'



function SecuredRoute({ auth, role, children, ...props }) {
    return (
        <Route {...props}>
            {hasRole(auth, role) ? children : <Redirect to="/" />}
        </Route >

    )
}

export default SecuredRoute