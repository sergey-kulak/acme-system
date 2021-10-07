import { combineReducers } from 'redux'
import auth from './security/authReducer'
import toastNotification from './toastNotification'
import rsocket from './rsocket'

export default combineReducers({
    auth: auth,
    toast: toastNotification,
    rsocket: rsocket
})