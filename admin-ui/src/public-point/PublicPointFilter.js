import { DebounceInput } from 'react-debounce-input';
import { useState } from 'react';
import CompanySelect from "../company/CompanySelect";
import PublicPointStatusSelect from './PublicPointStatusSelect';

function PublicPointFilter({ isAdmin, filter, onChange }) {
    const [statuses, setStatuses] = useState(filter.status);

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value));
    }

    function handleCompanyChange(companyId) {
        onChange(filter.withNewValue('companyId', companyId));
    }

    function onStatusBlur() {
        onChange(filter.withNewValue('status', statuses));
    }
    return (
        <div className="form-row">
            {isAdmin && <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="company">Company</label>
                <CompanySelect name="company" onChange={handleCompanyChange}
                    value={filter.companyId} isClearable
                    showTypeCheckBox />
            </div>}
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="namePattern">Name</label>
                <DebounceInput name="namePattern" onChange={handleChange}
                    value={filter.namePattern} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-5 col-md-6">
                <label htmlFor="status">Status</label>
                <PublicPointStatusSelect name="status" isMulti
                    onChange={setStatuses} closeMenuOnSelect={false}
                    onBlur={onStatusBlur}
                    value={statuses}>
                </PublicPointStatusSelect>
            </div>
        </div>
    );
}

export default PublicPointFilter;