import { useEffect, useState } from 'react';
import Select from 'react-select';
import { getValidationClass, hasValidationError } from '../utils';
import rfDataService from './rfDataService';

function CountrySelect({ value, field, form, onChange, ...props }) {
    const [options, setOptions] = useState([]);

    value = field && field.value ? field.value : value;

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



    function handleChange(selectedOption) {
        let selectedValue = selectedOption && selectedOption.value;
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedValue
                }
            };
            field.onChange(event);
        } else {
            onChange(selectedValue);
        }
    }


    let selected = value && options.find(option => option.value === value);
    const className = `flex-grow-1 ${getValidationClass(form, field)}`;

    return (
        <div className="cmt-select">
            <Select placeholder="" options={options} className={className}
                onChange={handleChange} {...props} isClearable
                value={selected} />
            {
                hasValidationError(form, field) &&
                <small className="form-text text-danger">
                    <span >
                        {form.errors[field.name]}
                    </span >
                </small >
            }
        </div>
    );
}

export default CountrySelect;