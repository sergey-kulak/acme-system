import restApi from '../restApi';
import { Cache } from '../cache';

const cache = new Cache();
const COUNTRY_REGION = 'countries';
const CURRENCY_REGION = 'currencies';
const LANG_REGION = 'languages';

const rfDataService = {
    findCountries: function () {
        return cache.retriveIfAbsent(COUNTRY_REGION, {},
            () => restApi.get('/rf-data-service/countries'));
    },
    findCurrencies: function () {
        return cache.retriveIfAbsent(CURRENCY_REGION, {},
            () => restApi.get('/rf-data-service/currencies'));
    },
    findLanguages: function () {
        return cache.retriveIfAbsent(LANG_REGION, {},
            () => restApi.get('/rf-data-service/languages'));
    }
}

export default rfDataService;