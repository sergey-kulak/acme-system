import restApi from '../common/restApi';

const BASE_URL = '/pp-service/public-point-tables';

const publicPointTableService = {
    find: function (publicPointId) {
        return restApi.get(BASE_URL, {
            params: { publicPointId }
        });
    },
    save: function (request) {
        return restApi.post(BASE_URL, request);
    }
}

export default publicPointTableService;