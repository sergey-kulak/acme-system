import axios from "axios"

const restApi = axios.create({
  baseURL: '/',
  headers: {
    "Content-Type": "application/json"
  }
})

restApi.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response.status === 401) {
      if (localStorage.getItem("token")) {
        localStorage.removeItem("token")
      } else {
        error.message = "Bad credentials"
        return Promise.reject(error)
      }
    } else if (error.response.status === 400) {
      error.message = "Bad request"
      return Promise.reject(error)
    } else if (error.response.status === 500) {
      error.message = "Server error"
      return Promise.reject(error)
    } else {
      return Promise.reject(error)
    }
  }
)

export default restApi
