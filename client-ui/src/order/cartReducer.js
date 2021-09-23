export const ACTIONS = {
    ADD: 'ADD',
    REMOVE: 'REMOVE',
    UPDATE: 'UPDATE'
}

const buildState = () => {
    return JSON.parse(localStorage.getItem("cart")) || [];
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

const reducer = (state = INIT_STATE, action) => {
    switch (action.type) {
        case ACTIONS.ADD: {
            let newItem = action.payload;
            let found = state.find(item => item.dishId === newItem.dishId);
            if (found) {
                found.quantity += newItem.quantity;
            } else {
                state = [...state, action.payload];
            }
            break;
        }
        case ACTIONS.UPDATE: {
            let newItem = action.payload;
            state = state
                .map(item => item.dishId === newItem.dishId ? newItem : item);
            break;
        }
        default:
            let dishId = action.payload;
            state = state.filter(item => item.dishId !== dishId);
            break;

    }
    localStorage.setItem("cart", JSON.stringify(state));

    return state;
}
export default reducer;