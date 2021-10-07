import { useHistory } from "react-router-dom"

function useHistoryBack(defaultPath) {
    const history = useHistory()

    return () => {
        if (history.length > 1) {
            history.goBack()
        } else {
            history.push(defaultPath)
        }
    }
}

export default useHistoryBack