import restApi from '../common/restApi';

const BASE_URL = '/menu-service/categories';

const categoryService = {
    find: function (filter) {
        return restApi.get(BASE_URL, {
            params: filter
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
    updateOrder: function (request) {
        return restApi.post(`${BASE_URL}/order`, request) ;
    }
}

export default categoryService;