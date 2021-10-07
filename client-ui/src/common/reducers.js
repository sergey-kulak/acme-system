import { combineReducers } from 'redux';
import auth from './security/authReducer'
import toastNotification from './toastNotification'
import cart from '../order/cartReducer';
import rsocket from './rsocket'

export default combineReducers({
    auth: auth,
    toast: toastNotification,
    cart: cart,
    rsocket: rsocket
});