import restApi from '../restApi';
import { Cache } from '../cache';

const cache = new Cache();
const COUNTRY_REGION = 'countries';
const CURRENCY_REGION = 'currencies';
const LANG_REGION = 'languages';

const BASE_URL = '/rf-data-service';

const rfDataService = {
    findCountries: function () {
        return cache.retriveIfAbsent(COUNTRY_REGION, {},
            () => restApi.get(`${BASE_URL}/countries`));
    },
    findCurrencies: function () {
        return cache.retriveIfAbsent(CURRENCY_REGION, {},
            () => restApi.get(`${BASE_URL}/currencies`));
    },
    findLangs: function () {
        return cache.retriveIfAbsent(LANG_REGION, {},
            () => restApi.get(`${BASE_URL}/langs`));
    }
}

export default rfDataService;