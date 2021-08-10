import RestApi from './RestApi';


const AuthService = {
    login: function (request) {
        RestApi.defaults.headers.common["Authorization"] = null;
        return RestApi.post('/user-service/auth/login', request);
    },
    refreshAccessToken: function () {
        return RestApi.post('/user-service/auth/refresh');
    }
}

export default AuthService;