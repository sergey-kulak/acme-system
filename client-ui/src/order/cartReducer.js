export const ACTIONS = {
    ADD: 'ADD',
    REMOVE: 'REMOVE',
    UPDATE: 'UPDATE',
    CLEAR: 'CLEAR_CART',
    SET_ORDER_ID: 'SET_ORDER_ID',
    RELOAD: 'RELOAD',
}

const buildState = () => {
    return JSON.parse(localStorage.getItem("cart")) || { items: [] };
}

const INIT_STATE = buildState();

export const onAdd = (data) => {
    return dispatch => dispatch({ type: ACTIONS.ADD, payload: data })
}

export const onRemove = (dishId) => {
    return dispatch => dispatch({ type: ACTIONS.REMOVE, payload: dishId });
}

export const onUpdate = (data) => {
    return dispatch => dispatch({ type: ACTIONS.UPDATE, payload: data });
}

export const onClearCart = () => {
    return dispatch => dispatch({ type: ACTIONS.CLEAR, payload: {} });
}

export const onSetOrderId = (orderId) => {
    return dispatch => dispatch({ type: ACTIONS.SET_ORDER_ID, payload: orderId });
}

export const onReload = () => {
    return dispatch => dispatch({ type: ACTIONS.RELOAD });
}

const reducer = (state = INIT_STATE, action) => {
    switch (action.type) {
        case ACTIONS.ADD: {
            let newItem = action.payload;
            let found = state.items.find(item => item.dishId === newItem.dishId);
            if (found) {
                found.quantity += newItem.quantity;
            } else {
                state.items.push(action.payload);
                state = { ...state };
            }
            break;
        }
        case ACTIONS.UPDATE: {
            let newItem = action.payload;
            state.items = state.items
                .map(item => item.dishId === newItem.dishId ? newItem : item);
            state = { ...state };
            break;
        }
        case ACTIONS.CLEAR: {
            state = { items: [] };
            break;
        }
        case ACTIONS.SET_ORDER_ID: {
            state = { items: [], orderId: action.payload };
            break;
        }
        case ACTIONS.RELOAD: {
            state = { ...state, orderUpdateTime: new Date() };
            break;
        }
        default:
            let dishId = action.payload;
            state.items = state.items.filter(item => item.dishId !== dishId);
            state = { ...state };
            break;
    }
    localStorage.setItem("cart", JSON.stringify(state));

    return state;
}

export default reducer;