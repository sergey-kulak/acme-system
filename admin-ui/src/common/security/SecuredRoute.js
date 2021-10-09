import { Route, Redirect } from "react-router-dom"
import { hasRole } from './index'



function SecuredRoute({ auth, role, children, ...props }) {
    const roles = Array.isArray(role) ? role : [role]
    const hasAcces = hasRole(auth, ...roles)
    return (
        <Route {...props}>
            {hasAcces ? children : <Redirect to="/" />}
        </Route >

    )
}

export default SecuredRoute