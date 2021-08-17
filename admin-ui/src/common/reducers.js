import { combineReducers } from 'redux';
import auth from './security/authReducer'
import toastNotification from './toastNotification'

export default combineReducers({
    auth: auth,
    toast: toastNotification
});