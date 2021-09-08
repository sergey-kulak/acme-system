import { useEffect, useState } from 'react';
import Select from '../Select';
import rfDataService from './rfDataService';

function CountrySelect(props) {
    const [options, setOptions] = useState([]);

    useEffect(() => {
        function mapToOption(country) {
            return {
                value: country.code,
                label: country.name,
                data: country
            };
        }

        rfDataService.findCountries()
            .then(response => response.data)
            .then(data => setOptions(data.map(mapToOption)));
    }, [])

    return (
        <Select options={options} {...props} />
    );
}

export default CountrySelect;