
import restApi from '../restApi'
import jwtDecode from "jwt-decode"

export const ACTIONS = {
    LOGIN_SUCCESS: 'LOGIN_SUCCESS',
    LOGOUT: 'LOGOUT'
}
const buildState = () => {
    let accessToken = localStorage.getItem("accessToken")
    let state = { isAuthenticated: false }
    if (accessToken) {
        let decodedToken = jwtDecode(accessToken)
        if (decodedToken.exp < (new Date().getTime() / 1000)) {
            console.warn("Token is expired")
            localStorage.removeItem("accessToken")
        } else {
            state.accessToken = accessToken
            state.isAuthenticated = true
            state.user = decodedToken
            restApi.defaults.headers.common["Authorization"] = "Bearer " + accessToken
        }
    }
    return state
}

const INIT_STATE = buildState()

export const onLogin = (data) => {
    return dispatch => dispatch({ type: ACTIONS.LOGIN_SUCCESS, payload: data })
}

export const onLogout = () => {
    return dispatch => dispatch({ type: ACTIONS.LOGOUT, payload: {} })
}

const reducer = (state = INIT_STATE, action) => {
    switch (action.type) {
        case ACTIONS.LOGIN_SUCCESS: {
            const accessToken = action.payload.accessToken
            localStorage.setItem("accessToken", accessToken)

            return buildState()
        }
        case ACTIONS.LOGOUT: {
            localStorage.removeItem("accessToken")
            restApi.defaults.headers.common["Authorization"] = null

            return buildState()
        }
        default:
            return state
    }
}
export default reducer