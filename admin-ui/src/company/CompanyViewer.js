import { useCallback, useEffect, useState } from "react";
import { useParams, Link, Redirect } from "react-router-dom";
import * as Icon from 'react-feather';
import { useIntl } from 'react-intl';
import { connect } from 'react-redux';
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage } from "../common/utils";
import ChoosePlanDialog from '../plan/ChoosePlanDialog';
import companyPlanService from '../plan/companyPlanService';
import userService from '../user/userService';
import { hasRole, ROLE } from "../common/security";
import companyService from './companyService';
import BackButton from "../common/BackButton";

function CompanyViewer({ auth, onSuccess, onError }) {
    const params = useParams();
    const isAdmin = hasRole(auth, ROLE.ADMIN);
    const id = isAdmin ? params.id : auth.user.cmpid;
    const intl = useIntl();
    const [company, setCompany] = useState();
    const [owners, setOwners] = useState([]);
    const [plan, setPlan] = useState();
    const [planHistory, setPlanHistory] = useState([]);
    const [expandGI, setExpandGI] = useState(false);
    const [expandPlan, setExpandPlan] = useState(false);
    const [showPlanDialog, setShowPlanDialog] = useState(false);

    useEffect(() => {
        companyService.findByIdFullDetails(id)
            .then(response => {
                setCompany(response.data);
            })
    }, [id]);

    function loadOwners() {
        userService.findOwners(id)
            .then(response => setOwners(response.data.content));
    }

    const loadActivePlan = useCallback(() => {
        companyPlanService.findActivePlan(id)
            .then(response => setPlan(response.data));
    }, [id]);

    useEffect(() => {
        loadActivePlan();
    }, [id, loadActivePlan]);

    function loadPlanHistory() {
        companyPlanService.getHistory(id)
            .then(response => setPlanHistory(response.data));
    }

    function onPlanClick(e) {
        e.preventDefault();
        setShowPlanDialog(true);
    }

    function onPlanChange(planId) {
        setShowPlanDialog(false);
        if (planId && planId !== (plan && plan.id)) {
            companyPlanService.assignPlan(company.id, planId)
                .then(() => {
                    loadActivePlan();
                    if (expandPlan) {
                        loadPlanHistory();
                    }
                    onSuccess("Plan was assigned successfully");
                }, error => {
                    onError(getErrorMessage(error.response.data));
                });
        }
    }

    function statusLabel() {
        return intl.formatMessage({ id: `company.status.${company.status.toLowerCase()}` })
    }

    function countryLabel() {
        return intl.formatMessage({ id: `country.${company.country}` })
    }

    function planLabel(plan) {
        return plan ?
            `${plan.name} (${plan.maxTableCount} tables, ${plan.monthPrice} ${plan.currency})` :
            'Not assigned';
    }

    function onExpandGI() {
        let newExpandGI = !expandGI;
        if (newExpandGI && !owners.length) {
            loadOwners();
        }
        setExpandGI(newExpandGI);
    }

    function onExpandPlan() {
        let newExpandPlan = !expandPlan;
        if (newExpandPlan && !planHistory.length) {
            loadPlanHistory();
        }
        setExpandPlan(newExpandPlan);
    }

    function wrapValue(value) {
        return value || '-';
    }

    const labelClass = "col-sm-4 col-md-2 col-form-label text-sm-right font-italic";
    const controlClass = "col-sm-8 col-md-4";

    if (!id) {
        return <Redirect to="/" />
    }

    return (
        <div className="main-content">
            {company && <div>
                <div className="main-content-title mb-2">
                    "{company.fullName}" company
                </div>
                <div className="main-content-body">
                    <div className="d-flex align-items-center mb-2">
                        <button className="btn btn-light cmt-btn mr-3" onClick={onExpandGI}>
                            {expandGI ? <Icon.ChevronsUp className="filter-icon" /> :
                                <Icon.ChevronsDown className="filter-icon" />}
                        </button>
                        <h4 className="h4 mb-0">General info</h4>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="fullName" className={labelClass}>Full name:</label>
                        <div className={controlClass}>
                            <input readOnly value={company.fullName} name="fullName"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="status" className={labelClass}>Status:</label>
                        <div className={controlClass}>
                            <input readOnly value={statusLabel()} name="status"
                                type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="vatin" className={labelClass}>VAT:</label>
                        <div className={controlClass}>
                            <input readOnly value={company.vatin} name="vatin"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="regNumber" className={labelClass}>Registration number:</label>
                        <div className={controlClass}>
                            <input readOnly value={wrapValue(company.regNumber)} name="regNumber"
                                type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    {expandGI && <>
                        <div className="form-group row mb-0">
                            <label htmlFor="country" className={labelClass}>Country:</label>
                            <div className={controlClass}>
                                <input readOnly value={countryLabel()} name="country"
                                    type="text" className="form-control-plaintext" />
                            </div>
                            <label htmlFor="city" className={labelClass}>City:</label>
                            <div className={controlClass}>
                                <input readOnly value={company.city} name="city"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="address" className={labelClass}>Address:</label>
                            <div className={controlClass}>
                                <input readOnly value={company.address} name="address"
                                    type="text" className="form-control-plaintext" />
                            </div>
                            <label htmlFor="email" className={labelClass}>Email:</label>
                            <div className={controlClass}>
                                <input readOnly value={company.email} name="email"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="phone" className={labelClass}>Phone:</label>
                            <div className={controlClass}>
                                <input readOnly value={company.phone} name="phone"
                                    type="text" className="form-control-plaintext" />
                            </div>
                            <label htmlFor="site" className={labelClass}>Site:</label>
                            <div className={controlClass}>
                                <input readOnly value={company.site} name="site"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="phone" className={labelClass}>Owners:</label>
                            <div className="col-sm-8 col-md-10">
                                {
                                    owners.map(owner => <div key={owner.id}>
                                        {owner.lastName} {owner.firstName}, {owner.email}
                                    </div>)
                                }
                            </div>
                        </div>
                    </>}
                    <div className="d-flex align-items-center mb-2">
                        <button className="btn btn-light cmt-btn mr-3" onClick={onExpandPlan}>
                            {expandPlan ? <Icon.ChevronsUp className="filter-icon" /> :
                                <Icon.ChevronsDown className="filter-icon" />}
                        </button>
                        <h4 className="h4 mb-0">Plans</h4>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="phone" className={labelClass}>Current plan:</label>
                        <div className="col-form-label col-sm-6 col-md-4">
                            <a href="/" onClick={onPlanClick}>
                                {planLabel(plan)}
                            </a>
                        </div>
                    </div>
                    {expandPlan && planHistory.length > 0 && <>
                        <div className="form-group row mb-0">
                            <label className={labelClass}>History:</label>
                            <div className="col-sm-8 col-md-10">
                                <table className="table table-striped table-hover table-sm">
                                    <thead>
                                        <tr>
                                            <th>Plan</th>
                                            <th>Start date</th>
                                            <th>End date</th>
                                        </tr>
                                    </thead>
                                    <tbody>{
                                        planHistory.map(phItem => <tr key={phItem.id}>
                                            <td>{planLabel(phItem.plan)}</td>
                                            <td>{phItem.startDate}</td>
                                            <td>{phItem.endDate}</td>
                                        </tr>)
                                    }</tbody>
                                </table>
                            </div>
                        </div>
                    </>}
                    <div className="mt-2 mb-0">
                        {isAdmin && <Link to={`/companies/${id}`} className="btn btn-primary mr-2">
                            Edit
                        </Link>}
                        <BackButton defaultPath="/">Back</BackButton>
                    </div>
                    <div>
                        {showPlanDialog
                            && <ChoosePlanDialog show={showPlanDialog}
                                company={company} onClose={onPlanChange}
                            />
                        }
                    </div>
                </div>
            </div>}
        </div >
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(CompanyViewer);