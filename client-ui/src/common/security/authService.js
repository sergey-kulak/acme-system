import restApi from '../restApi'

const authService = {
    login: function (request) {
        restApi.defaults.headers.common["Authorization"] = null
        return restApi.post('/pp-service/api/public-point-tables/login', request)
    }
}

export default authService