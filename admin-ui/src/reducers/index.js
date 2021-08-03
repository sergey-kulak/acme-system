import { combineReducers } from 'redux';
import Auth from './Auth'
import ToastNotification from './ToastNotification'

export default combineReducers({
    auth: Auth,
    toast: ToastNotification
});