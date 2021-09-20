import { Field, Form, Formik } from 'formik';
import { useCallback, useEffect, useRef, useState } from "react";
import { connect } from 'react-redux';
import { Redirect, useLocation, useParams } from "react-router-dom";
import { v4 as uuidv4 } from 'uuid';
import * as Yup from 'yup';
import BackButton from "../common/BackButton";
import fileService from '../common/fileService';
import HighlightInput from '../common/HighlightInput';
import ImageUpload from '../common/ImageUpload';
import { hasRole, ROLE } from '../common/security';
import { onError, onSuccess } from '../common/toastNotification';
import useHistoryBack from '../common/useHistoryBack';
import { getErrorMessage } from "../common/utils";
import CompanySelect from '../company/CompanySelect';
import PublicPointSelect from '../public-point/PublicPointSelect';
import dishService from './dishService';
import TagSelect from './TagSelect';

function DishEditor({ auth, onSuccess, onError }) {
    const { id } = useParams();
    const { state } = useLocation();
    const isCreate = id === 'new';
    const [dish, setDish] = useState();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        composition: '',
        tags: [],
        companyId: state && state.companyId,
        publicPointId: state && state.publicPointId,
    });
    const [image, setImage] = useState();
    const [imageUrl, setImageUrl] = useState();
    const formikRef = useRef(null);
    const historyBack = useHistoryBack("/dishes");

    const validationSchema = Yup.object({
        name: Yup.string().required('Required'),
        description: Yup.string().required('Required'),
        composition: Yup.string().required('Required'),
        companyId: Yup.string().required('Required'),
        publicPointId: Yup.string().required('Required')
    });

    const loadImage = useCallback((data) => {
        let imageKey = data.primaryImage;
        let request = {
            companyId: data.companyId,
            publicPointId: data.publicPointId,
            action: 'DOWNLOAD',
            imageKeys: [imageKey]
        }

        return fileService.getDishImageUrls(request)
            .then(response => response.data[imageKey])
            .then(url => {
                setImageUrl(url);
                return data;
            })
    }, [])

    useEffect(() => {
        if (!isCreate) {
            dishService.findFullDetailsById(id)
                .then(response => response.data)
                .then(loadImage)
                .then(data => {
                    setDish(data);
                    toFormData(data);
                });
        }
    }, [id, isCreate, loadImage]);

    function toFormData(dish) {
        setFormData({
            name: dish.name,
            description: dish.description,
            composition: dish.composition,
            tags: dish.tags,
            companyId: dish.companyId,
            publicPointId: dish.publicPointId
        });
    }

    function uploadImage() {
        let imageKey = `${dish && dish.id ? dish.id : uuidv4()}/${uuidv4()}.jpg`;

        let request = {
            companyId: formData.companyId,
            publicPointId: formData.publicPointId,
            action: 'UPLOAD',
            imageKeys: [imageKey]
        }
        return fileService.getDishImageUrls(request)
            .then(response => response.data[imageKey])
            .then(url => fileService.upload(image, url)
                .then(() => imageKey))
    }

    function save(request, primaryImage) {
        request.primaryImage = primaryImage;
        if (isCreate) {
            return dishService.create(request)
                .then(() => {
                    onSuccess(`${request.name} dish was created successfuly`);
                    historyBack();
                }, error => {
                    let errorMessage = error.response.data.error;
                    onError(errorMessage || 'Error');
                })
        } else {
            delete request.companyId;
            delete request.publicPointId;

            return dishService.update(dish.id, request)
                .then(() => {
                    onSuccess(`${request.name} dish was updated successfuly`);
                    historyBack();
                }, error => onError(getErrorMessage(error.response.data)))
        }
    }

    function onSubmit(formData) {
        let request = { ...formData };
        let imagePromise = image ? uploadImage() : Promise.resolve(dish.primaryImage)
        imagePromise.then(imageKey => save(request, imageKey));
    }

    function onFileChange(file) {
        setImage(file);
        setImageUrl(URL.createObjectURL(file))
    }

    if (isCreate && (!state.companyId || !state.publicPointId)) {
        return <Redirect to="/dishes" />
    }

    const canEdit = hasRole(auth, ROLE.PP_MANAGER);

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {dish ? dish.name : 'Dish creation'}
                </div>
                {(isCreate || !!dish) &&
                    <div className="main-content-body">
                        <Formik innerRef={formikRef}
                            enableReinitialize
                            initialValues={formData}
                            validationSchema={validationSchema}
                            onSubmit={onSubmit}>
                            <Form noValidate={true}>
                                <div className="form-row">
                                    <div className="form-group col-md-6 mb-0">
                                        <div className="form-group col-12 pl-0">
                                            <label htmlFor="name">Name</label>
                                            <Field component={HighlightInput} name="name"
                                                readOnly={!canEdit}
                                                className="form-control" />
                                        </div>
                                        <div className="form-group col-12 pl-0">
                                            <label htmlFor="composition">Composition</label>
                                            <Field component={HighlightInput} name="composition"
                                                readOnly={!canEdit}
                                                tag="textarea" rows={5} className="form-control" />
                                        </div>
                                    </div>
                                    <div className="form-group col-md-6 mb-0">
                                        <div className="d-flex justify-content-center">
                                            <ImageUpload src={imageUrl} onChange={onFileChange}
                                                isDisabled={!canEdit} />
                                        </div>
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group col-12">
                                        <label htmlFor="description">Description</label>
                                        <Field component={HighlightInput} name="description"
                                            readOnly={!canEdit}
                                            tag="textarea" rows={2} className="form-control" />
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group col-12">
                                        <label htmlFor="tags">Tags</label>
                                        <Field component={TagSelect} name="tags"
                                            companyId={formData.companyId} isDisabled={!canEdit}
                                            publicPointId={formData.publicPointId} />
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
                                {canEdit && <button type="submit" className="btn btn-primary mr-2">Save</button>}
                                <BackButton defaultPath="/dishes">Back</BackButton>
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
})(DishEditor);