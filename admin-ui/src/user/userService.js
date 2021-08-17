import restApi, { buildGetFilterParams } from '../common/restApi';
import { ROLE } from '../common/security';


const userService = {
    find: function (filter, pageable, sort) {
        return restApi.get('/user-service/users', {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    findById: function (id) {
        return restApi.get(`/user-service/users/${id}`);
    },
    update: function (id, request) {
        return restApi.put(`/user-service/users/${id}`, request);
    },
    create: function (request) {
        return restApi.post(`/user-service/users`, request);
    },
    findOwners: function (companyId) {
        let params = new URLSearchParams();
        params.append('companyId', companyId);
        params.append('role', ROLE.COMPANY_OWNER);
        params.append('size', 30);
        params.append('sort', 'last_name,asc');

        return restApi.get('/user-service/users', {
            params: params
        });
    }
}

export default userService;