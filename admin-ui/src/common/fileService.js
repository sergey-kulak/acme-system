import restApi  from '../common/restApi'
import axios from "axios"

const IMAGE_BASE_URL = '/file-service/api/images'
const awsApi = axios.create()

const fileService = {
    upload: function (file, url) {
        return awsApi.put(url, file, {
            headers: {
                "Content-Type": "image/jpg",
            }
        })
    },
    getDishImageUrls: function (request) {
        return restApi.post(`${IMAGE_BASE_URL}/dish`, request)
    }
}

export default fileService