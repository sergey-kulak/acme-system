import restApi, { buildGetFilterParams } from '../common/restApi';

const BASE_URL = '/accounting-service/plans';

const planService = {
    create: function (request) {
        return restApi.post(BASE_URL, request);
    },
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    changeStatus: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}/status`, request);
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`);
    },
    update: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}`, request);
    },
    findActive: function (country) {
        return restApi.get(`${BASE_URL}/active`, {
            params: { country: country }
        });
    },
    findStatistics: function (id) {
        return restApi.get(`${BASE_URL}/${id}/statistics`);
    },
}

export default planService;