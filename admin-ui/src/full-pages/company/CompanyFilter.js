import { DebounceInput } from 'react-debounce-input';
import CompanyStatusSelect from '../../common/CompanyStatusSelect';
import { useState } from 'react';

function CompanyFilter({ filter, onChange }) {
    const [statuses, setStatuses] = useState(filter.status);

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value));
    }

    function onBlur() {
        onChange(filter.withNewValue('status', statuses));
    }

    return (
        <div className="form-row">
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="namePattern">Name</label>
                <DebounceInput name="namePattern" onChange={handleChange}
                    value={filter.namePattern} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="vatin">VATIN</label>
                <DebounceInput name="vatin" onChange={handleChange}
                    value={filter.vatin} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-2 col-md-6">
                <label htmlFor="country">Country</label>
                <DebounceInput name="country" onChange={handleChange}
                    value={filter.country} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-4 col-md-6">
                <label htmlFor="status">Status</label>
                <CompanyStatusSelect name="status" isMulti
                    onChange={setStatuses} closeMenuOnSelect={false}
                    onBlur={onBlur}
                    selectedStatuses={statuses}>
                </CompanyStatusSelect>
            </div>
        </div>
    );
}

export default CompanyFilter;