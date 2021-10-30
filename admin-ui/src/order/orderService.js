import restApi, { buildGetFilterParams } from '../common/restApi'

const BASE_URL = '/pp-service/api/orders'

const orderService = {
    find: function (filter, pageable, sort) {
        return restApi.get(BASE_URL, {
            params: buildGetFilterParams(filter, pageable, sort)
        })
    },
    findLive: function (filter) {
        return restApi.get(`${BASE_URL}/live`, {
            params: buildGetFilterParams(filter)
        })
    },
    findById: function (id) {
        return restApi.get(`${BASE_URL}/${id}`)
    },
    changeOrderStatus: function (id, status) {
        return restApi.put(`${BASE_URL}/${id}/status`, { status })
    }
    ,
    changeItemStatus: function (id, status) {
        return restApi.put(`${BASE_URL}/items/${id}/status`, { status })
    }
}

export default orderService