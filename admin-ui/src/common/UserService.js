import RestApi, { buildGetFilterParams } from './RestApi';


const UserService = {
    find: function (filter, pageable, sort) {
        return RestApi.get('/user-service/users', {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    findById: function (id) {
        return RestApi.get(`/user-service/users/${id}`);
    },
    update: function (id, request) {
        return RestApi.put(`/user-service/users/${id}`, request);
    },
    create: function (request) {
        return RestApi.post(`/user-service/users`, request);
    }
}

export default UserService;