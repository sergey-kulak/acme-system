import restApi from '../common/restApi'

const BASE_URL = '/accounting-service/public-point-plans'

const publicPointPlanService = {
    findActivePlan: function (publicPointId) {
        return restApi.get(`${BASE_URL}/active`, {
            params: { publicPointId }
        })
    },
    findActivePlanId: function (publicPointId) {
        return restApi.get(`${BASE_URL}/active/id`, {
            params: { publicPointId }
        })
    },
    assignPlan(request) {
        return restApi.post(BASE_URL, request)
    },
    getHistory(publicPointId) {
        return restApi.get(`${BASE_URL}/history`, {
            params: { publicPointId }
        })
    }
}

export default publicPointPlanService