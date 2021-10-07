import { Form } from 'react-bootstrap'
import { DebounceInput } from 'react-debounce-input'
import { hasRole, ROLE } from '../common/security'
import CompanySelect from '../company/CompanySelect'
import PublicPointSelect from '../public-point/PublicPointSelect'

function DishFilter({ auth, filter, onChange }) {

    function onCompanyChange(companyId) {
        onChange(filter.withNewValue('companyId', companyId))
    }

    function onPublicPointChange(publicPointId) {
        onChange(filter.withNewValue('publicPointId', publicPointId))
    }

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value))
    }

    function onWithDeletedChange(e) {
        onChange(filter.withNewValue('withDeleted', e.target.checked))
    }

    return (
        <div className="table-filter">
            <div className="form-row">
                {hasRole(auth, ROLE.ADMIN) && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Company</label>
                    <CompanySelect name="company" isClearable showTypeCheckBox
                        value={filter.companyId} onChange={onCompanyChange} />
                </div>}
                {hasRole(auth, ROLE.COMPANY_OWNER) && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Public point</label>
                    <PublicPointSelect name="pp" isClearable auth={auth}
                        isDisabled={!filter.companyId} companyId={filter.companyId}
                        value={filter.publicPointId} onChange={onPublicPointChange} />
                </div>}
                <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="namePattern">Name</label>
                    <DebounceInput name="namePattern" onChange={handleChange}
                        value={filter.namePattern} debounceTimeout="300"
                        readOnly={!filter.publicPointId}
                        type="text" className="form-control" />
                </div>
                <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="namePattern">Show deleted</label>
                    <Form.Check
                        type="switch" checked={!!filter.publicPointId && filter.withDeleted} 
                        disabled={!filter.publicPointId}
                        id="withDeleted" onChange={onWithDeletedChange}/>
                </div>
            </div>
        </div>
    )
}

export default DishFilter