import restApi, { buildGetFilterParams } from '../common/restApi';

const BASE_URL = '/pp-service/public-points';

const publicPointService = {
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`);
    },
    findFullDetailsById: function (id) {
        return restApi.get(`${BASE_URL}/${id}/full-details`);
    },
    update: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}`, request);
    },
    create: function (request) {
        return restApi.post(BASE_URL, request);
    },
    findNames: function (companyId) {
        return restApi.get(`${BASE_URL}/names`, {
            params: { companyId }
        });
    },
    changeStatus: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}/status`, request);
    }
}

export default publicPointService;