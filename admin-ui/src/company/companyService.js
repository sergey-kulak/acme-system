import { Cache } from '../common/cache';
import restApi, { buildGetFilterParams } from '../common/restApi';

const cache = new Cache();
const COMPANY_NAME_REGION = 'cmp-names';

const companyService = {
    register: function (request) {
        return restApi.post('/user-service/companies', request)
            .then(response => {
                cache.invalidate(COMPANY_NAME_REGION)
                return response;
            });
    },
    find: function (filter, pageable, sort) {
        return restApi.get('/user-service/companies', {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    changeStatus: function (id, request) {
        return restApi.put(`/user-service/companies/${id}/status`, request)
            .then(response => {
                cache.invalidate(COMPANY_NAME_REGION)
                return response;
            });
    },
    findById: function (id) {
        return restApi.get(`/user-service/companies/${id}`);
    },
    findByIdFullDetails: function (id) {
        return restApi.get(`/user-service/companies/${id}/full-details`);
    },
    update: function (id, request) {
        return restApi.put(`/user-service/companies/${id}`, request);
    }
    ,
    findNames: function (statuses) {
        let urlSearchParams = new URLSearchParams();
        if (statuses && statuses.length) {
            statuses.forEach(status => {
                urlSearchParams.append("status", status);
            });
        }

        let provider = () => restApi.get(`/user-service/companies/names`, { params: urlSearchParams });
        return cache.retriveIfAbsent(COMPANY_NAME_REGION, urlSearchParams.toString(),
            provider, 30
        )
    }
}

export default companyService;