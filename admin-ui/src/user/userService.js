import restApi, { buildGetFilterParams } from '../common/restApi'

const BASE_URL = '/user-service/api/users'

const userService = {
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        })
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`)
    },
    update: function (id, request) {
        return restApi.put(`${BASE_URL}/${id}`, request)
    },
    create: function (request) {
        return restApi.post(BASE_URL, request)
    },
    findNames: function (request) {
        return restApi.get(`${BASE_URL}/names`, {
            params: request
        })
    }

}

export default userService