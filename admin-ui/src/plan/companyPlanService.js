import restApi from '../common/restApi';

const BASE_URL = '/accounting-service/company-plans';

const companyPlanService = {
    findActivePlan: function (companyId) {
        return restApi.get(`${BASE_URL}/active`, {
            params: { companyId }
        });
    },
    assignPlan(companyId, planId) {
        return restApi.post(BASE_URL, { companyId, planId });
    },
    getHistory(companyId) {
        return restApi.get(`${BASE_URL}/history`, {
            params: { companyId }
        });
    }
}

export default companyPlanService;