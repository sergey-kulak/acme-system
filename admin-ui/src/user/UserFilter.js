import { DebounceInput } from 'react-debounce-input'
import { useState } from 'react'
import UserRoleSelect from './UserRoleSelect'
import CompanySelect from "../company/CompanySelect"
import UserStatusSelect from './UserStatusSelect'

function UserFilter({ isAdmin, filter, onChange }) {
    const [statuses, setStatuses] = useState(filter.status)
    const [roles, setRoles] = useState(filter.role)

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value))
    }

    function handleCompanyChange(companyId) {
        onChange(filter.withNewValue('companyId', companyId))
    }

    function onStatusBlur() {
        onChange(filter.withNewValue('status', statuses))
    }

    function onRoleBlur() {
        onChange(filter.withNewValue('role', roles))
    }

    return (
        <div className="form-row">
            {isAdmin && <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="company">Company</label>
                <CompanySelect name="company" onChange={handleCompanyChange}
                    value={filter.companyId} isClearable={true}
                    showTypeCheckBox={true} />
            </div>}
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="vatin">Email</label>
                <DebounceInput name="email" onChange={handleChange}
                    value={filter.email} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="country">Role</label>
                <UserRoleSelect name="role" isMulti
                    onChange={setRoles} closeMenuOnSelect={false}
                    onBlur={onRoleBlur}
                    value={roles}>
                </UserRoleSelect>
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="status">Status</label>
                <UserStatusSelect name="status" isMulti
                    onChange={setStatuses} closeMenuOnSelect={false}
                    onBlur={onStatusBlur}
                    value={statuses}>
                </UserStatusSelect>
            </div>
        </div>
    )
}

export default UserFilter