import RestApi, { buildGetFilterParams } from './RestApi';


const CompanyService = {
    register: function (request) {
        return RestApi.post('/user-service/companies', request);
    },
    find: function (filter, pageable, sort) {
        return RestApi.get('/user-service/companies', {
            params: buildGetFilterParams(filter, pageable, sort)
        });
    }
}

export default CompanyService;