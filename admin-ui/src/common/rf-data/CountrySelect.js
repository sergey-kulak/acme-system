import { useEffect, useState } from 'react';
import Select from 'react-select';
import { getValidationClass, hasValidationError } from '../utils';
import rfDataService from './rfDataService';

function CountrySelect({ value, field, form, onChange, ...props }) {
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



    function handleChange(selectedOption) {
        let selectedValue
        if (selectedOption) {
            selectedValue = props.isMulti ?
                selectedOption.map(item => item.value) : selectedOption.value;
        }
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedValue
                }
            };
            field.onChange(event);
        }
        if (onChange) {
            onChange(selectedValue);
        }
    }

    value = field && field.value ? field.value : value;
    let selected = props.isMulti ?
        options.filter(option => value.includes(option.value)) :
        options.filter(option => value === option.value);
    const className = `flex-grow-1 ${getValidationClass(form, field)}`;

    return (
        <div className={`${props.className} cmt-select`}>
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