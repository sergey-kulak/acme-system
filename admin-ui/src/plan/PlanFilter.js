import { DebounceInput } from 'react-debounce-input';
import { useState } from 'react';
import PlanStatusSelect from './PlanStatusSelect';
import CountrySelect from '../common/rf-data/CountrySelect';

function PlanFilter({ filter, onChange }) {
    const [statuses, setStatuses] = useState(filter.status);

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value));
    }

    function onBlur() {
        onChange(filter.withNewValue('status', statuses));
    }

    function onCountryChange(country) {
        onChange(filter.withNewValue('country', country));
    }

    function onOnlyGlobalChange(e) {
        onChange(filter.withNewValue('onlyGlobal', e.target.checked));
    }

    return (
        <div className="form-row">
            <div className="form-group col-lg-4 col-md-6">
                <label htmlFor="namePattern">Name</label>
                <DebounceInput name="namePattern" onChange={handleChange}
                    value={filter.namePattern} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-1 col-md-6">
                <label htmlFor="tableCount">Tables</label>
                <DebounceInput name="tableCount" onChange={handleChange}
                    value={filter.tableCount} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="country">Country</label>
                <div className="d-flex align-items-center">
                    <input type="checkbox" checked={filter.onlyGlobal} className="mr-1"
                        onChange={onOnlyGlobalChange} />
                    <CountrySelect name="country" onChange={onCountryChange}
                        value={filter.country} className="flex-grow-1"
                        type="text" isDisabled={filter.onlyGlobal}/>
                </div>
            </div>
            <div className="form-group col-lg-4 col-md-6">
                <label htmlFor="status">Status</label>
                <PlanStatusSelect name="status" isMulti
                    onChange={setStatuses} closeMenuOnSelect={false}
                    onBlur={onBlur}
                    value={statuses}>
                </PlanStatusSelect>
            </div>
        </div>
    );
}

export default PlanFilter;