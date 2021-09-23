import restApi from '../common/restApi';

const BASE_URL = '/menu-service/menu';
const DISH_BASE_URL = '/menu-service/dishes';

const menuService = {
    getCategories: function () {
        return restApi.get(`${BASE_URL}/categories`);
    },
    findDishes: function (categoryId) {
        return restApi.get(`${BASE_URL}/dishes`, {
            params: { categoryId }
        });
    },
    findDish: function (id) {
        return restApi.get(`${DISH_BASE_URL}/${id}`);
    }
}

export default menuService;