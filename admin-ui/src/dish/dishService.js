import restApi, { buildGetFilterParams } from '../common/restApi'

const BASE_URL = '/menu-service/api/dishes'

const dishService = {
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        })
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`)
    },
    findFullDetailsById: function (id) {
        return restApi.get(`${BASE_URL}/${id}/full-details`)
    },
    update: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}`, request)
    },
    create: function (request) {
        return restApi.post(BASE_URL, request)
    },
    delete: function (id) {
        return restApi.delete(`${BASE_URL}/${id}`)
    },
    findTags(companyId, publicPointId) {
        return restApi.get(`${BASE_URL}/tags`, {
            params: { companyId, publicPointId }
        })
    },
    findNames(companyId, publicPointId) {
        return restApi.get(`${BASE_URL}/names`, {
            params: { companyId, publicPointId }
        })
    }
}

export default dishService