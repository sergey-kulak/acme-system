import ReactSelect from 'react-select';
import { hasValidationError, getValidationClass } from './utils';

function Select({ options, value, optionFilter, onChange, field, form, ...props }) {

    function handleChange(selectedOptions) {
        let selectedValues
        if (selectedOptions) {
            selectedValues = props.isMulti ?
                selectedOptions.map(option => option.value) : selectedOptions.value;
        }
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedValues
                }
            };
            field.onChange(event);
        }
        if (onChange) {
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
            <ReactSelect options={allowedOptions} placeholder=""
                onChange={handleChange}
                {...props} className={className}
                value={selectedOptions}>
            </ReactSelect>
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

export default Select;