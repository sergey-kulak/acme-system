import * as utils from './utils'

export class Pageable {
    static URL_PARAM_PAGE = 'page'
    static URL_PARAM_SIZE = 'size'

    constructor(page, size) {
        this.page = page
        this.size = size
    }

    toUrlParams() {
        return {
            [Pageable.URL_PARAM_PAGE]: this.page,
            [Pageable.URL_PARAM_SIZE]: this.size,
        }
    }

    static fromUrlParams(urlSearchParams) {
        return new Pageable(
            Number(urlSearchParams.get(Pageable.URL_PARAM_PAGE) || '1'),
            Number(urlSearchParams.get(Pageable.URL_PARAM_SIZE) || '5'),
        )
    }
}

export class Sort {
    static URL_PARAM = 'sort'

    constructor(field, direction) {
        this.field = field
        this.direction = direction
    }

    toUrlParams() {
        return {
            [Sort.URL_PARAM]: `${this.field},${this.direction}`
        }
    }

    static fromUrlParams(urlSearchParams, defaultField, defaultDirection) {
        let paramValue = urlSearchParams.get(Sort.URL_PARAM)
        if (paramValue) {
            let values = paramValue.split(',')
            return new Sort(values[0], values[1] || 'asc')
        } else {
            return new Sort(defaultField, defaultDirection || 'asc')
        }
    }
}

export const combineAsUrlParams = (...paramObjects) => {
    let query = ''
    if (paramObjects && paramObjects.length) {
        for (let paramObj of paramObjects) {
            let params = (paramObj.toUrlParams && paramObj.toUrlParams()) || {}
            utils.clearEmptyProps(params)

            for (let param in params) {
                let paramValue = params[param]
                if (!Array.isArray(paramValue)) {
                    paramValue = [paramValue]
                }
                for (let singleValue of paramValue) {
                    if (query.length) {
                        query += '&'
                    }
                    query += `${param}=${singleValue}`
                }
            }
        }
    }

    return '?' + query
}