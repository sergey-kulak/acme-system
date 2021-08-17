import restApi from '../restApi';

const authService = {
    login: function (request) {
        restApi.defaults.headers.common["Authorization"] = null;
        return restApi.post('/user-service/auth/login', request);
    },
    refreshAccessToken: function () {
        return restApi.post('/user-service/auth/refresh');
    }
}

export default authService;