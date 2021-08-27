import { Field, Form, Formik } from 'formik';
import { useEffect, useState } from "react";
import { connect } from 'react-redux';
import { useParams, Link } from "react-router-dom";
import * as Yup from 'yup';
import BackButton from "../common/BackButton";
import companyService from './companyService';
import userService from '../user/userService';
import HighlightInput from '../common/HighlightInput';
import useHistoryBack from '../common/useHistoryBack';
import { onError, onSuccess } from '../common/toastNotification';
import CountrySelect from '../common/rf-data/CountrySelect';
import './CompanyEditor.css';

function CompanyEditor({ onSuccess, onError }) {
    const { id } = useParams();
    const [company, setCompany] = useState();
    const [formData, setFormData] = useState({
        city: '',
        address: '',
        site: '',
        email: '',
        phone: ''
    });
    const [owners, setOwners] = useState([]);
    const historyBack = useHistoryBack("/companies");

    const validationSchema = Yup.object({
        city: Yup.string().required('Required'),
        address: Yup.string().required('Required'),
        email: Yup.string().email('Invalid email address'),
        phone: Yup.string().required('Required')
    });

    useEffect(() => {
        companyService.findByIdFullDetails(id)
            .then(response => {
                setCompany(response.data);
                toFormData(response.data);
            })
    }, [id]);

    useEffect(() => {
        userService.findOwners(id)
            .then(response => setOwners(response.data.content));
    }, [id]);

    function toFormData(company) {
        setFormData({
            city: company.city,
            address: company.address,
            site: company.site || '',
            email: company.email,
            phone: company.phone || ''
        });
    }

    function onSubmit(formData) {
        companyService.update(company.id, formData)
            .then(() => {
                onSuccess(`${company.fullName} was updated successfuly`);
                historyBack();
            }, error => {
                let errorMessage = error.response.data.error;
                onError(errorMessage || 'Error');
            })
    }

    return (
        <div className="main-content">
            {company && <div>
                <div className="main-content-title mb-2">
                    "{company.fullName}" company
                </div>
                <div className="main-content-body">
                    <Formik enableReinitialize
                        initialValues={formData}
                        validationSchema={validationSchema}
                        onSubmit={onSubmit}>
                        <Form noValidate={true}>
                            <div className="form-group">
                                <label htmlFor="fullName">Full name</label>
                                <input readOnly value={company.fullName} name="fullName"
                                    type="text" className="form-control" />
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="vatin">VAT identification number</label>
                                    <input readOnly value={company.vatin} name="vatin"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="regNumber">Registration number</label>
                                    <input readOnly value={company.regNumber || ''} name="regNumber"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="country">Country</label>
                                    <CountrySelect isDisabled value={company.country} name="country" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="city">City</label>
                                    <Field component={HighlightInput} name="city"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <div className="form-group">
                                <label htmlFor="address">Address</label>
                                <Field component={HighlightInput} name="address"
                                    tag="textarea" rows={2} className="form-control" />
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-4">
                                    <label htmlFor="companyEmail">Email</label>
                                    <Field component={HighlightInput} name="email"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-4">
                                    <label htmlFor="companyPhone">Phone</label>
                                    <Field component={HighlightInput} name="phone"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-4">
                                    <label htmlFor="site">Site</label>
                                    <Field component={HighlightInput} name="site"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <h4 className="h4 mb-3">Owners:</h4>
                            <div className="form-row">
                                <ul className="owner-list">{
                                    owners.map(owner => <li key={owner.id}>
                                        <Link to={`/users/${owner.id}`}>
                                            {owner.lastName} {owner.firstName}, {owner.email}
                                        </Link>
                                    </li>)
                                }</ul>
                            </div>
                            <button type="submit" className="btn btn-primary mr-2">Save</button>
                            <BackButton defaultPath="/companies">Back</BackButton>
                        </Form>
                    </Formik>
                </div>
            </div>}
        </div>
    );
}

const mapStateToProps = () => {
    return {};
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(CompanyEditor);