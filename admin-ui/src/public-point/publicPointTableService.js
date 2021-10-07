import restApi from '../common/restApi';

const BASE_URL = '/pp-service/public-point-tables';

const publicPointTableService = {
    find: function (publicPointId) {
        return restApi.get(BASE_URL, {
            params: { publicPointId }
        });
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`);
    },
    save: function (request) {
        return restApi.post(BASE_URL, request);
    },
    getClientUiUrl: function (id) {
        return restApi.get(`${BASE_URL}/${id}/client-ui-url`);
    },
}

export default publicPointTableService;