import { DebounceInput } from 'react-debounce-input';
import Select from 'react-select';

const options = [
    { value: 'INACTIVE', label: 'INACTIVE' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'SUSPENDED', label: 'SUSPENDED' },
    { value: 'STOPPED', label: 'STOPPED' }
];

function CompanyFilter({ filter, onChange }) {

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value));
    }

    function handleStatusChange(selectedOptions) {
        let selectedValues = selectedOptions.map(option => option.value);
        onChange(filter.withNewValue('status', selectedValues));
    }

    const selectedOptions = options.filter(option => filter.status.includes(option.value));

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
                <Select name="status" isMulti
                    options={options} onChange={handleStatusChange}
                    defaultValue={selectedOptions}>
                </Select>
            </div>
        </div>
    );
}

export default CompanyFilter;