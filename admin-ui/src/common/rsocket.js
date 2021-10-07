const INIT_STATE = {
    isOnline: false
}

export const ACTIONS = {
    CHANGE: 'CHANGE'
}

export const setOnline = (isOnline) => {
    return dispatch =>
        dispatch({ type: ACTIONS.CHANGE, payload: isOnline })
}

const reducer = (state = INIT_STATE, action) => {
    switch (action.type) {
        case ACTIONS.CHANGE: {
            return {
                isOnline: action.payload
            }
        }
        default:
            return state
    }
}

export default reducer