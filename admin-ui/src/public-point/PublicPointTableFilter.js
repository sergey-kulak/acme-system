import CompanySelect from '../company/CompanySelect';
import PublicPointSelect from './PublicPointSelect';

function PublicPointTableFilter({ isAdmin, filter, plan, onChange }) {

    function onCompanyChange(companyId) {
        onChange(filter.withNewValue('companyId', companyId));
    }

    function onPublicPointChange(publicPointId) {
        onChange(filter.withNewValue('publicPointId', publicPointId));
    }

    function planLabel(plan) {
        return plan ?
            `${plan.name} (${plan.maxTableCount} tables, ${plan.monthPrice} ${plan.currency})` :
            'Not assigned';
    }

    return (
        <div className="table-filter">
            <div className="form-row">
                {isAdmin && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Company</label>
                    <CompanySelect name="company" isClearable showTypeCheckBox
                        value={filter.companyId} onChange={onCompanyChange} />
                </div>}
                <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Public point</label>
                    <PublicPointSelect name="pp" isClearable
                        isDisabled={!filter.companyId} companyId={filter.companyId}
                        value={filter.publicPointId} onChange={onPublicPointChange} />
                </div>
                {!!filter.publicPointId && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="phone">Current plan</label>
                    <input readOnly value={planLabel(plan)}
                        type="text" className="form-control-plaintext" />
                </div>}
            </div>
        </div>
    )
}

export default PublicPointTableFilter;