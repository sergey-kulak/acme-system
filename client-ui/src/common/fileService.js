import restApi  from '../common/restApi'

const IMAGE_BASE_URL = '/file-service/api/images'

const fileService = {
    getDishImageUrls: function (request) {
        return restApi.post(`${IMAGE_BASE_URL}/dish`, request)
    }
}

export default fileService