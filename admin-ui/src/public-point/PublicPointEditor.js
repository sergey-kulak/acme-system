import { Field, Form, Formik } from 'formik';
import { useEffect, useRef, useState } from "react";
import { connect } from 'react-redux';
import { useParams } from "react-router-dom";
import * as Yup from 'yup';
import BackButton from "../common/BackButton";
import publicPointService from './publicPointService';
import HighlightInput from '../common/HighlightInput';
import useHistoryBack from '../common/useHistoryBack';
import { onError, onSuccess } from '../common/toastNotification';
import CompanySelect from '../company/CompanySelect';
import { hasRole, ROLE } from "../common/security";
import { getErrorMessage } from "../common/utils";
import LangSelect from '../common/rf-data/LangSelect';

function PublicPointEditor({ auth, onSuccess, onError }) {
    const { id } = useParams();
    const isCreate = id === 'new';
    const [publicPoint, setPublicPoint] = useState();
    const formikRef = useRef(null);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        city: '',
        address: '',
        primaryLang: '',
        langs: [],
        companyId: hasRole(auth, ROLE.ADMIN) ? '' : auth.user.cmpid
    });
    const historyBack = useHistoryBack("/public-points");

    const validationSchema = Yup.object({
        name: Yup.string().required('Required'),
        description: Yup.string(),
        city: Yup.string().required('Required'),
        address: Yup.string().required('Required'),
        primaryLang: Yup.string().required('Required'),
        langs: Yup.array().of(Yup.string()),
        companyId: Yup.string().required('Required')
    });

    useEffect(() => {
        if (!isCreate) {
            publicPointService.findByIdFullDetails(id)
                .then(response => {
                    setPublicPoint(response.data);
                    toFormData(response.data);
                });
        }
    }, [id, isCreate]);

    function toFormData(publicPoint) {
        setFormData({
            name: publicPoint.name,
            description: publicPoint.description,
            city: publicPoint.city,
            address: publicPoint.address,
            primaryLang: publicPoint.primaryLang,
            langs: publicPoint.langs.filter(lang => lang !== publicPoint.primaryLang),
            companyId: publicPoint.companyId
        })
    };

    function onSubmit(formData) {
        let request = { ...formData };
        if (isCreate) {
            publicPointService.create(request)
                .then(() => {
                    onSuccess(`${request.name} public point was created successfuly`);
                    historyBack();
                }, error => onError(getErrorMessage(error.response.data)))
        } else {
            publicPointService.update(publicPoint.id, request)
                .then(() => {
                    onSuccess(`${request.name} public point was updated successfuly`);
                    historyBack();
                }, error => onError(getErrorMessage(error.response.data)))
        }
    }

    function langFilter(options) {
        let formikValues = getFormikValues();
        let primaryLang = formikValues && formikValues.primaryLang;
        return options.filter(opt => opt.value !== primaryLang);
    }

    function getFormikValues() {
        return formikRef.current && formikRef.current.values;
    }

    function onPrimaryLangChange(newLang) {
        let formikValues = getFormikValues();
        let langs = formikValues.langs.filter(lang => lang !== newLang);

        setFormData({
            ...formikValues,
            primaryLang: newLang,
            langs: langs,
        });
    }

    function onCompanyChange(cmpId) {
        setFormData({
            ...getFormikValues(),
            companyId: cmpId
        });
    }

    const canSetCompany = hasRole(auth, ROLE.ADMIN);

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {publicPoint ? publicPoint.name : 'Public point creation'}
                </div>
                <div className="main-content-body">
                    <Formik innerRef={formikRef}
                        enableReinitialize
                        initialValues={formData}
                        validationSchema={validationSchema}
                        onSubmit={onSubmit}>
                        <Form noValidate={true}>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="name">Name</label>
                                    <Field component={HighlightInput} name="name"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="companyId">Company</label>
                                    <Field component={CompanySelect} name="companyId"
                                        isDisabled={!(isCreate && canSetCompany)}
                                        onChange={onCompanyChange}
                                    />
                                </div>
                            </div>
                            <div className="form-group">
                                <label htmlFor="description">Description</label>
                                <Field component={HighlightInput} name="description"
                                    tag="textarea" rows={2} className="form-control" />
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="city">City</label>
                                    <Field component={HighlightInput} name="city"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="address">Addres</label>
                                    <Field component={HighlightInput} name="address"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="primaryLang">Primary language</label>
                                    <Field component={LangSelect} name="primaryLang"
                                        onChange={onPrimaryLangChange} />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="langs">Additional languages</label>
                                    <Field component={LangSelect} name="langs" isMulti
                                        optionFilter={langFilter}/>
                                </div>
                            </div>
                            <button type="submit" className="btn btn-primary mr-2">Save</button>
                            <BackButton defaultPath="/public-points">Back</BackButton>
                        </Form>
                    </Formik>
                </div>
            </div>
        </div>
    );
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(PublicPointEditor);