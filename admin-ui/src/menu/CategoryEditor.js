import { Field, Form, Formik } from 'formik';
import { useEffect, useRef, useState } from "react";
import { connect } from 'react-redux';
import { Redirect, useLocation, useParams } from "react-router-dom";
import * as Yup from 'yup';
import BackButton from "../common/BackButton";
import HighlightInput from '../common/HighlightInput';
import TimeInput from '../common/TimeInput';
import { onError, onSuccess } from '../common/toastNotification';
import useHistoryBack from '../common/useHistoryBack';
import DaySelect from '../common/DaySelect';
import { deleteIfEmpty, getErrorMessage } from "../common/utils";
import CompanySelect from '../company/CompanySelect';
import PublicPointSelect from '../public-point/PublicPointSelect';
import categoryService from './categoryService';
import DishSelector from './DishSelector';
import { hasRole, ROLE } from '../common/security';

function CategoryEditor({ auth, onSuccess, onError }) {
    const { id } = useParams();
    const { state } = useLocation();
    const isCreate = id === 'new';
    const [category, setCategory] = useState();
    const [formData, setFormData] = useState({
        name: '',
        startTime: '',
        endTime: '',
        days: [],
        dishIds: [],
        companyId: state && state.companyId,
        publicPointId: state && state.publicPointId,
    });
    const formikRef = useRef(null);
    const historyBack = useHistoryBack("/menu");

    const validationSchema = Yup.object({
        name: Yup.string().required('Required'),
        dishIds: Yup.array().of(Yup.string()).min(1, 'Required'),
        publicPointId: Yup.string().required('Required')
    });

    useEffect(() => {
        if (!isCreate) {
            categoryService.findById(id)
                .then(response => response.data)
                .then(data => {
                    setCategory(data);
                    toFormData(data);
                });
        }
    }, [id, isCreate]);

    function toFormData(category) {
        setFormData({
            name: category.name,
            startTime: category.startTime,
            endTime: category.endTime,
            days: category.days,
            dishIds: category.dishIds,
            companyId: category.companyId,
            publicPointId: category.publicPointId
        });
    }

    function onSubmit(formData) {
        let request = { ...formData };
        deleteIfEmpty(request, 'startTime');
        deleteIfEmpty(request, 'endTime');
        if (isCreate) {
            return categoryService.create(request)
                .then(() => {
                    onSuccess(`${request.name} category was created successfuly`);
                    historyBack();
                }, error => {
                    let errorMessage = error.response.data.error;
                    onError(errorMessage || 'Error');
                })
        } else {
            delete request.companyId;
            delete request.publicPointId;

            return categoryService.update(category.id, request)
                .then(() => {
                    onSuccess(`${request.name} category was updated successfuly`);
                    historyBack();
                }, error => onError(getErrorMessage(error.response.data)))
        }
    }

    if (isCreate && (!state.companyId || !state.publicPointId)) {
        return <Redirect to="/menu" />
    }

    const canEdit = hasRole(auth, ROLE.PP_MANAGER);

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {category ? category.name : 'Category creation'}
                </div>
                {(isCreate || !!category) &&
                    <div className="main-content-body">
                        <Formik innerRef={formikRef}
                            enableReinitialize
                            initialValues={formData}
                            validationSchema={validationSchema}
                            onSubmit={onSubmit}>
                            <Form noValidate={true}>
                                <div className="form-row">
                                    <div className="form-group col-12">
                                        <label htmlFor="name">Name</label>
                                        <Field component={HighlightInput} name="name"
                                            readOnly={!canEdit}
                                            className="form-control" />
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group col-2">
                                        <label htmlFor="startTime">Start time</label>
                                        <Field component={TimeInput} name="startTime"
                                            readOnly={!canEdit}
                                            className="form-control" />
                                    </div>
                                    <div className="form-group col-2">
                                        <label htmlFor="endTime">End time</label>
                                        <Field component={TimeInput} name="endTime"
                                            readOnly={!canEdit}
                                            className="form-control" />
                                    </div>
                                    <div className="form-group col-8">
                                        <label htmlFor="endTime">Week days</label>
                                        <Field component={DaySelect} name="days" isMulti
                                            isDisabled={!canEdit} />
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group col-md-6">
                                        <label htmlFor="companyId">Company</label>
                                        <Field component={CompanySelect} name="companyId"
                                            isDisabled
                                        />
                                    </div>
                                    <div className="form-group col-md-6">
                                        <label htmlFor="publicPointId">Public point</label>
                                        <Field component={PublicPointSelect} name="publicPointId"
                                            isDisabled auth={auth}
                                            companyId={formData.companyId}
                                        />
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group col-12">
                                        <Field component={DishSelector} name="dishIds"
                                            companyId={formData.companyId} readOnly={!canEdit}
                                            publicPointId={formData.publicPointId} />
                                    </div>
                                </div>
                                {canEdit && <button type="submit" className="btn btn-primary mr-2">Save</button>}
                                <BackButton defaultPath="/menu">Back</BackButton>
                            </Form>
                        </Formik>
                    </div>}
            </div>
        </div>
    );
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(CategoryEditor);