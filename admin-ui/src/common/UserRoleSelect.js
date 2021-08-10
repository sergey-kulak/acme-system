import React from 'react';
import Select from 'react-select';
import { hasValidationError, getValidationClass } from '../common/Utils';

const options = [
    { value: 'ADMIN', label: 'Admin' },
    { value: 'COMPANY_OWNER', label: 'Company owner' },
    { value: 'PP_MANAGER', label: 'PP manager' },
    { value: 'WAITER', label: 'Waiter' },
    { value: 'ACCOUNTANT', label: 'Accountant' },
    { value: 'COOK', label: 'Cook' }
];
function UserRoleSelect({ value, optionFilter, onChange, field, form, ...props }) {

    function handleStatusChange(selectedOptions) {
        let selectedValues = props.isMulti ?
            selectedOptions.map(option => option.value) : selectedOptions.value;
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedValues
                }
            };
            field.onChange(event);
        } else {
            onChange(selectedValues);
        }
    }

    value = field && field.value ? field.value : value;
    const selectedOptions = props.isMulti ?
        options.filter(option => value.includes(option.value)) :
        options.filter(option => value === option.value);

    const className = `${props.className || ''} ${getValidationClass(form, field)}`
    const allowedOptions = optionFilter ? optionFilter(options) : options;

    return (
        <div className="cmt-select">
            <Select options={allowedOptions} placeholder=""
                onChange={handleStatusChange}
                {...props} className={className}
                value={selectedOptions}>
            </Select>
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

export default UserRoleSelect;