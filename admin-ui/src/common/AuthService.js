import RestApi from './RestApi';


const AuthService = {
    login: function (request) {
        RestApi.defaults.headers.common["Authorization"] = null;
        return RestApi.post('/user-service/auth/login', request);
    }
}

export default AuthService;