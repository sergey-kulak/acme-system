import { useCallback, useEffect, useState } from "react";
import * as Icon from 'react-feather';
import { FormattedDate, useIntl } from 'react-intl';
import { connect } from 'react-redux';
import { Link, Redirect, useParams } from "react-router-dom";
import BackButton from "../common/BackButton";
import { hasRole, ROLE } from "../common/security";
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage } from "../common/utils";
import companyService from '../company/companyService';
import publicPointPlanService from '../plan/publicPointPlanService';
import userService from '../user/userService';
import ChangePublicPointStatusDialog from "./ChangePublicPointStatusDialog";
import publicPointService from './publicPointService';
import PublicPointStatusLabel from "./PublicPointStatusLabel";
import ChoosePlanDialog from '../plan/ChoosePlanDialog';

function PublicPointViewer({ auth, onSuccess, onError }) {
    const params = useParams();
    const isAdmin = hasRole(auth, ROLE.ADMIN);
    const id = params.id;
    const intl = useIntl();
    const [publicPoint, setPublicPoint] = useState();
    const [company, setCompany] = useState();
    const [managers, setManagers] = useState([]);
    const [plan, setPlan] = useState();
    const [planHistory, setPlanHistory] = useState([]);
    const [expandGI, setExpandGI] = useState(false);
    const [expandPlan, setExpandPlan] = useState(false);
    const [showPlanDialog, setShowPlanDialog] = useState(false);
    const [showStatusChangeDialog, setShowStatusChangeDialog] = useState(false);

    const loadPublicPoint = useCallback(() => {
        publicPointService.findByIdFullDetails(id)
            .then(response => setPublicPoint(response.data))
    }, [id]);

    useEffect(() => {
        loadPublicPoint();
    }, [id, loadPublicPoint]);

    useEffect(() => {
        if (publicPoint) {
            companyService.findByIdFullDetails(publicPoint.companyId)
                .then(response => setCompany(response.data))
        }
    }, [publicPoint]);

    function loadManagers() {
        if (publicPoint) {
            let request = {
                companyId: publicPoint.companyId,
                publicPointId: publicPoint.id,
                role: ROLE.PP_MANAGER
            };
            userService.findNames(request)
                .then(response => setManagers(response.data || []));
        }
    }

    const loadActivePlan = useCallback(() => {
        publicPointPlanService.findActivePlan(id)
            .then(response => setPlan(response.data));
    }, [id]);

    useEffect(() => {
        loadActivePlan();
    }, [id, loadActivePlan]);

    function loadPlanHistory() {
        publicPointPlanService.getHistory(id)
            .then(response => response.data)
            .then(data => data.map(item => ({
                ...item,
                startDate: new Date(item.startDate),
                endDate: item.endDate && new Date(item.endDate)
            })))
            .then(setPlanHistory);
    }

    function onPlanClick(e) {
        e.preventDefault();
        setShowPlanDialog(true);
    }

    function onPlanChange(planId) {
        setShowPlanDialog(false);
        if (planId && planId !== (plan && plan.id)) {
            let request = {
                planId,
                publicPointId: publicPoint.id,
                companyId: publicPoint.companyId,
            }
            publicPointPlanService.assignPlan(request)
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

    function onPublicPointStatusClick(e) {
        e.preventDefault();
        setShowStatusChangeDialog(true);
    }

    function onStatusChange(newStatus) {
        setShowStatusChangeDialog(false);
        if (newStatus) {
            publicPointService.changeStatus(publicPoint.id, { status: newStatus })
                .then(() => {
                    onSuccess("Public point status was changed successfully");
                    loadPublicPoint();
                    if (newStatus === 'STOPPED') {
                        loadActivePlan();
                        if (expandPlan) {
                            loadPlanHistory()
                        }
                    }
                }, error => {
                    onError(getErrorMessage(error.response.data));
                });
        }
    }

    function planLabel(plan) {
        return plan ?
            `${plan.name} (${plan.maxTableCount} tables, ${plan.monthPrice} ${plan.currency})` :
            'Not assigned';
    }

    function onExpandGI() {
        let newExpandGI = !expandGI;
        if (newExpandGI && !managers.length) {
            loadManagers();
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

    function langLabel(lang) {
        return intl.formatMessage({ id: `lang.${lang}` })
    }

    function primaryLangLabel() {
        return langLabel(publicPoint.primaryLang)
    }

    function addLangLabels() {
        return wrapValue(publicPoint.langs.map(langLabel).join(", "));
    }

    function currencyLabel() {
        let crName = intl.formatMessage({ id: `currency.name.${publicPoint.currency}` });
        let crSymbol = intl.formatMessage({ id: `currency.symbol.${publicPoint.currency}` });
        return `${crName}, ${crSymbol}`;
    }

    const labelClass = "col-sm-4 col-md-2 col-form-label text-sm-right font-italic";
    const controlClass = "col-sm-8 col-md-4";

    if (!id) {
        return <Redirect to="/" />
    }

    return (
        <div className="main-content">
            {publicPoint && <div>
                <div className="main-content-title mb-2">
                    "{publicPoint.name}" public point
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
                        <label htmlFor="name" className={labelClass}>Name:</label>
                        <div className={controlClass}>
                            <input readOnly value={publicPoint.name} name="name"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="status" className={labelClass}>Status:</label>
                        <div className="col-form-label col-sm-6 col-md-4">
                            {publicPoint.status === 'STOPPED' ?
                                <PublicPointStatusLabel status={publicPoint.status} showText /> :
                                <a href="#status" onClick={onPublicPointStatusClick}>
                                    <PublicPointStatusLabel status={publicPoint.status} showText />
                                </a>
                            }
                        </div>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="company" className={labelClass}>Company:</label>
                        <div className={controlClass}>
                            <input readOnly value={(company && company.fullName) || ''} name="company"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="description" className={labelClass}>Description:</label>
                        <div className={controlClass}>
                            <input readOnly value={wrapValue(publicPoint.description)} name="description"
                                type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    {expandGI && <>
                        <div className="form-group row mb-0">
                            <label htmlFor="city" className={labelClass}>City:</label>
                            <div className={controlClass}>
                                <input readOnly value={publicPoint.city} name="city"
                                    type="text" className="form-control-plaintext" />
                            </div>
                            <label htmlFor="address" className={labelClass}>Address:</label>
                            <div className={controlClass}>
                                <input readOnly value={publicPoint.address} name="address"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="address" className={labelClass}>Primary language:</label>
                            <div className={controlClass}>
                                <input readOnly value={primaryLangLabel()} name="primaryLang"
                                    type="text" className="form-control-plaintext" />
                            </div>
                            <label htmlFor="email" className={labelClass}>Additional languages:</label>
                            <div className={controlClass}>
                                <input readOnly value={addLangLabels()} name="langs"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="currency" className={labelClass}>Currency:</label>
                            <div className={controlClass}>
                                <input readOnly value={currencyLabel()} name="Currency"
                                    type="text" className="form-control-plaintext" />
                            </div>
                        </div>
                        <div className="form-group row mb-0">
                            <label htmlFor="phone" className={labelClass}>Managers:</label>
                            <div className="col-sm-8 col-md-10">
                                {
                                    managers.map(owner => <div key={owner.id}>
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
                            {publicPoint.status === 'STOPPED' ? planLabel(plan) :
                                <a href="/" onClick={onPlanClick}>
                                    {planLabel(plan)}
                                </a>}
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
                                            <td>
                                                <FormattedDate value={phItem.startDate} />
                                            </td>
                                            <td>
                                                {phItem.endDate &&
                                                    <FormattedDate value={phItem.endDate} />}
                                            </td>
                                        </tr>)
                                    }</tbody>
                                </table>
                            </div>
                        </div>
                    </>}
                    <div className="mt-2 mb-0">
                        {isAdmin && <Link to={`/public-points/${id}`} className="btn btn-primary mr-2">
                            Edit
                        </Link>}
                        <BackButton defaultPath="/">Back</BackButton>
                    </div>
                    {<div>
                        {showPlanDialog && company
                            && <ChoosePlanDialog show={showPlanDialog}
                                country={company.country} onClose={onPlanChange}
                            />
                        }
                    </div>}
                    {<div>
                        {showStatusChangeDialog
                            && <ChangePublicPointStatusDialog show={showStatusChangeDialog}
                                isAdmin={isAdmin}
                                status={publicPoint.status} onClose={onStatusChange}
                            />
                        }
                    </div>}
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
})(PublicPointViewer);