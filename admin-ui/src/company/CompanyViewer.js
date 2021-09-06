import { useCallback, useEffect, useState } from "react";
import * as Icon from 'react-feather';
import { useIntl } from 'react-intl';
import { connect } from 'react-redux';
import { Link, Redirect, useParams } from "react-router-dom";
import BackButton from "../common/BackButton";
import { hasRole, ROLE } from "../common/security";
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage } from "../common/utils";
import userService from '../user/userService';
import ChangeCompanyStatusDialog from "./ChangeCompanyStatusDialog";
import companyService from './companyService';
import CompanyStatusLabel from "./CompanyStatusLabel";

function CompanyViewer({ auth, onSuccess, onError }) {
    const params = useParams();
    const isAdmin = hasRole(auth, ROLE.ADMIN);
    const id = isAdmin ? params.id : auth.user.cmpid;
    const intl = useIntl();
    const [company, setCompany] = useState();
    const [owners, setOwners] = useState([]);
    const [expandGI, setExpandGI] = useState(false);
    const [showStatusChangeDialog, setShowStatusChangeDialog] = useState(false);

    const loadCompany = useCallback(() => {
        companyService.findByIdFullDetails(id)
            .then(response => setCompany(response.data))
    }, [id]);

    useEffect(() => {
        loadCompany();
    }, [id, loadCompany]);

    function loadOwners() {
        let request = {
            companyId: id,
            role: ROLE.COMPANY_OWNER
        };
        userService.findNames(request)
            .then(response => setOwners(response.data || []));
    }

    function onCompanyStatusClick(e) {
        e.preventDefault();
        setShowStatusChangeDialog(true);
    }

    function onStatusChange(newStatus) {
        setShowStatusChangeDialog(false);
        if (newStatus) {
            companyService.changeStatus(company.id, { status: newStatus })
                .then(() => {
                    onSuccess("Company status was changed successfully");
                    loadCompany();
                }, error => {
                    onError(getErrorMessage(error.response.data));
                });
        }
    }

    function countryLabel() {
        return intl.formatMessage({ id: `country.${company.country}` })
    }

    function onExpandGI() {
        let newExpandGI = !expandGI;
        if (newExpandGI && !owners.length) {
            loadOwners();
        }
        setExpandGI(newExpandGI);
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
                        <div className="col-form-label col-sm-6 col-md-4">
                            {company.status === 'STOPPED' ?
                                <CompanyStatusLabel status={company.status} showText /> :
                                <a href="#status" onClick={onCompanyStatusClick}>
                                    <CompanyStatusLabel status={company.status} showText />
                                </a>
                            }
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
                    <div className="mt-2 mb-0">
                        {isAdmin && <Link to={`/companies/${id}`} className="btn btn-primary mr-2">
                            Edit
                        </Link>}
                        <BackButton defaultPath="/">Back</BackButton>
                    </div>
                    <div>
                        {showStatusChangeDialog
                            && <ChangeCompanyStatusDialog show={showStatusChangeDialog}
                                status={company.status} onClose={onStatusChange}
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