import RestApi, { buildGetFilterParams } from './RestApi';


const CompanyService = {
    register: function (request) {
        return RestApi.post('/user-service/companies', request);
    },
    find: function (filter, pageable, sort) {
        return RestApi.get('/user-service/companies', {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    },
    changeStatus: function (id, request) {
        return RestApi.put(`/user-service/companies/${id}/status`, request);
    },
    findById: function (id) {
        return RestApi.get(`/user-service/companies/${id}/full-details`);
    },
    update: function (id, request) {
        return RestApi.put(`/user-service/companies/${id}`, request);
    }
    ,
    findNames: function (statuses) {
        return RestApi.get(`/user-service/companies/names`, {
            params: { status: statuses }
        });
    }
}

export default CompanyService;