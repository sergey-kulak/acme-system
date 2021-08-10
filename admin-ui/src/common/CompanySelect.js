import { useEffect, useState } from 'react';
import Select from 'react-select';
import { getValidationClass, hasValidationError } from '../common/Utils';
import CompanyService from './CompanyService';

function CompanySelect({ value, showTypeCheckBox = false,
    field, form, onChange, ...props }) {
    const [onlyActive, setOnlyActive] = useState(true);
    const [options, setOptions] = useState([]);

    value = field && field.value ? field.value : value;

    useEffect(() => {
        function mapToOption(cmp) {
            let status = cmp.status.charAt(0);
            return {
                value: cmp.id,
                label: cmp.fullName
                    + (onlyActive || status === 'A' ? '' : ` (${status})`),
                data: cmp
            };
        }

        let promise;
        if (props.isDisabled && value) {
            promise = CompanyService.findById(value)
                .then(response => [response.data]);
        } else {
            let statuses = onlyActive ? ['ACTIVE'] : [];
            promise = CompanyService.findNames(statuses)
                .then(response => response.data);
        }

        promise.then(data => setOptions(data.map(mapToOption)));
    }, [onlyActive, props.isDisabled, value])



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
            <div className="d-flex align-items-center">
                {showTypeCheckBox && <input type="checkbox" checked={onlyActive} className="mr-1"
                    onChange={e => setOnlyActive(e.target.checked)} />}
                <Select placeholder="" options={options} className={className}
                    onChange={handleChange} {...props}
                    value={selected} />
            </div>
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

export default CompanySelect;