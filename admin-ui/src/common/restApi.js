import axios from "axios";

const restApi = axios.create({
  baseURL: '/',
  headers: {
    "Content-Type": "application/json"
  }
});

restApi.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response.status === 401) {
      if (localStorage.getItem("token")) {
        localStorage.removeItem("token");
      } else {
        error.message = "Bad credentials";
        return Promise.reject(error);
      }
    } else if (error.response.status === 400) {
      error.message = "Bad request";
      return Promise.reject(error);
    } else if (error.response.status === 500) {
      error.message = "Server error";
      return Promise.reject(error);
    } else {
      return Promise.reject(error);
    }
  }
);

export const buildGetFilterParams = (filter, pageable, sort) => {
  const params = { ...clean(filter), ...pageable, };
  params.page -= 1;
  if (sort) {
    params.sort = `${sort.field},${sort.direction}`;
  }

  let urlSearchParams = new URLSearchParams();
  for (var paramName in params) {
    let paramValue = params[paramName];
    if (!Array.isArray(paramValue)) {
      paramValue = [paramValue];
    }
    for (let singleValue of paramValue) {
      urlSearchParams.append(paramName, singleValue);
    }
  }

  return urlSearchParams;
}

function clean(obj) {
  for (var propName in obj) {
    if (obj[propName] === null || obj[propName] === undefined || obj[propName] === '') {
      delete obj[propName];
    }
  }
  return obj
}

export default restApi;
