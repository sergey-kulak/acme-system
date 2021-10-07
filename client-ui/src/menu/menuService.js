import restApi from '../common/restApi';

const BASE_URL = '/menu-service/menu';
const DISH_BASE_URL = '/menu-service/dishes';
const ORDER_BASE_URL = '/pp-service/orders';
const PP_BASE_URL = '/pp-service/public-points';

const menuService = {
    getCategories: () => {
        return restApi.get(`${BASE_URL}/categories`);
    },
    findDishes: (categoryId) => {
        return restApi.get(`${BASE_URL}/dishes`, {
            params: { categoryId }
        });
    },
    findDish: (id) => {
        return restApi.get(`${DISH_BASE_URL}/${id}`);
    },
    placeOrder: (request) => {
        return restApi.post(`${ORDER_BASE_URL}`, request);
    },
    findOrderById: (id) => {
        return restApi.get(`${ORDER_BASE_URL}/${id}`);
    },
    callWaiter: () => {
        return restApi.post(`${PP_BASE_URL}/call-waiter`);
    }
}

export default menuService;