import './TimeInput.css';

function TimeInput({ value, onChange, field, readOnly, form }) {
    value = field && field.value ? field.value : value;

    function onHourChange(e) {
        let hour = e.target.value;
        let min = value ? value.substring(getColonPos()) : ':00';
        fireChange(addLeadZero(hour) + min);
    }

    function addLeadZero(value) {
        return value.length === 1 ? '0' + value : value;
    }

    function removeLeadZero(value) {
        return value && value.startsWith('0') ? value.substring(1) : value;
    }

    function getColonPos() {
        return value.indexOf(':')
    }

    function fireChange(newValue) {
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: newValue
                }
            };
            field.onChange(event);
        }
        if (onChange) {
            onChange(newValue);
        }
    }

    function onMinChange(e) {
        let min = e.target.value;
        let hour = value ? value.substring(0, getColonPos() + 1) : '00:'
        fireChange(hour + addLeadZero(min));
    }

    function onBlur(e) {
        if (!e.target.value) {
            fireChange('');
        }
    }

    let [hour, min] = value ? value.split(':') : ['', '']
    return (
        <div className="time-input d-flex align-items-center">
            <input type="number" className="form-control" min="0" max="23" maxLength="2"
                readOnly={readOnly}
                value={removeLeadZero(hour)} onChange={onHourChange} onBlur={onBlur} />
            <span>:</span>
            <input type="number" className="form-control" min="0" max="59" maxLength="2"
                readOnly={readOnly}
                value={removeLeadZero(min)} onChange={onMinChange} onBlur={onBlur} />
        </div >
    )
}

export default TimeInput;