import restApi, { buildGetFilterParams } from '../common/restApi';
import { ROLE } from '../common/security';

const BASE_URL = '/user-service/users';

const userService = {
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`);
    },
    update: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}`, request);
    },
    create: function (request) {
        return restApi.post(BASE_URL, request);
    },
    findOwners: function (companyId) {
        let params = new URLSearchParams();
        params.append('companyId', companyId);
        params.append('role', ROLE.COMPANY_OWNER);
        params.append('size', 30);
        params.append('sort', 'last_name,asc');

        return restApi.get(BASE_URL, {
            params: params
        });
    }
}

export default userService;