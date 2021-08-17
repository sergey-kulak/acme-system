export const ACTIONS = {
    NOTIFY: 'NOTIFY',
    CLEAR: 'CLEAR'
}

export const onError = (message) => {
    return dispatch =>
        dispatch({
            type: ACTIONS.NOTIFY, payload: {
                message: message,
                type: 'error'
            }
        });
}

export const onSuccess = (message) => {
    return dispatch =>
        dispatch({
            type: ACTIONS.NOTIFY, payload: {
                message: message,
                type: 'success'
            }
        });
}

export const clearMessage = () => {
    return dispatch =>
        dispatch({
            type: ACTIONS.CLEAR, payload: {}
        });
}

const reducer = (state = {}, action) => {
    switch (action.type) {
        case ACTIONS.NOTIFY: {
            return action.payload;
        }
        case ACTIONS.CLEAR: {
            return {};
        }
        default:
            return state;
    }
}
export default reducer;