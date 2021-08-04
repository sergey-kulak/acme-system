import { useEffect, useState } from 'react';
import Select from 'react-select';
import CompanyService from './CompanyService';
import { hasValidationError, getValidationClass } from '../common/Utils';

function CompanySelect({ value, showTypeCheckBox = false, 
    field, form, onChange, ...props }) {
    const [onlyActive, setOnlyActive] = useState(true);
    const [options, setOptions] = useState([]);

    useEffect(() => {
        let statuses = onlyActive ? ['ACTIVE'] : [];
        CompanyService.findNames(statuses)
            .then(response => {
                let options = response.data.map(cmp => {
                    let status = cmp.status.charAt(0);
                    return {
                        value: cmp.id,
                        label: cmp.fullName
                            + (onlyActive || status === 'A' ? '' : ` (${status})`),
                        data: cmp
                    };
                });
                setOptions(options)
            });
    }, [onlyActive])

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

    value = field && field.value ? field.value : value;
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