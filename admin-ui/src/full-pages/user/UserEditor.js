import { Field, Form, Formik } from 'formik';
import { useEffect, useState } from "react";
import { connect } from 'react-redux';
import { useParams } from "react-router-dom";
import * as Yup from 'yup';
import BackButton from "../../common/BackButton";
import UserService from '../../common/UserService';
import HighlightInput from '../../common/HighlightInput';
import useHistoryBack from '../../common/useHistoryBack';
import { onError, onSuccess } from '../../reducers/ToastNotification';
import UserRoleSelect from '../../common/UserRoleSelect';
import CompanySelect from '../../common/CompanySelect';
import { hasRole, ROLE, getAllAccessibleRoles } from "../../common/security";

function UserEditor({ auth, onSuccess, onError }) {
    const { id } = useParams();
    const isCreate = id === 'new';
    const [user, setUser] = useState();
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        role: '',
        companyId: hasRole(auth, ROLE.ADMIN) ? '' : auth.user.cmpid
    });
    const historyBack = useHistoryBack("/users");

    const validationSchema = Yup.object({
        firstName: Yup.string().required('Required'),
        lastName: Yup.string().required('Required'),
        email: Yup.string().email('Invalid email address').required('Required'),
        password: passwordSchema(),
        confirmPassword: passwordSchema()
            .test('passwords-match', 'Passwords must match', function (value) {
                return this.parent.password === value
            }),
        role: Yup.string().required('Required'),
        companyId: Yup.string().required('Required')
    });

    function passwordSchema() {
        let pswSchema = Yup.string().min(6, 'Must be 6 characters or longer');
        if (isCreate) {
            pswSchema = pswSchema.required('Required');
        }
        return pswSchema;
    }

    useEffect(() => {
        if (!isCreate) {
            UserService.findById(id)
                .then(response => {
                    setUser(response.data);
                    toFormData(response.data);
                });
        }
    }, [id, isCreate]);

    function toFormData(user) {
        setFormData({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phone: user.phone || '',
            role: user.role || '',
            companyId: user.companyId || '',
            password: '',
            confirmPassword: '',
        });
    }

    function getFullName() {
        return `${user.lastName} ${user.firstName}`;
    }

    function onSubmit(formData) {
        let request = { ...formData };
        if (isCreate) {
            UserService.create(request)
                .then(() => {
                    onSuccess(`${request.lastName} ${request.firstName} user was created successfuly`);
                    historyBack();
                }, error => {
                    let errorMessage = error.response.data.error;
                    onError(errorMessage || 'Error');
                })
        } else {
            if (!request.password) {
                delete request.password;
                delete request.confirmPassword;
            }
            UserService.update(user.id, request)
                .then(() => {
                    onSuccess(`${getFullName()} user was updated successfuly`);
                    historyBack();
                }, error => {
                    let errorMessage = error.response.data.error;
                    onError(errorMessage || 'Error');
                })
        }
    }

    function roleFilter(options) {
        let roles = getAllAccessibleRoles(auth);
        return options.filter(option => roles.includes(option.value))
    }

    const canChangeRole = hasRole(auth, ROLE.ADMIN, ROLE.COMPANY_OWNER);
    const canSetCompany = hasRole(auth, ROLE.ADMIN);

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {user ? getFullName() : 'User creation'}
                </div>
                <div className="main-content-body">
                    <Formik enableReinitialize
                        initialValues={formData}
                        validationSchema={validationSchema}
                        onSubmit={onSubmit}>
                        <Form noValidate={true}>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="firstName">First name</label>
                                    <Field component={HighlightInput} name="firstName"
                                        type="text" className="form-control" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="lastName">Last name</label>
                                    <Field component={HighlightInput} name="lastName"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="email">Email</label>
                                    <Field component={HighlightInput} name="email"
                                        type="text" className="form-control"
                                        disabled={!isCreate} />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="phone">Phone</label>
                                    <Field component={HighlightInput} name="phone"
                                        type="text" className="form-control" />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="password">Password</label>
                                    <Field component={HighlightInput} name="password"
                                        type="password" className="form-control" />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="confirmPassword">Confirm password</label>
                                    <Field component={HighlightInput} name="confirmPassword"
                                        type="password" className="form-control" />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="confirmPassword">Role</label>
                                    <Field component={UserRoleSelect} name="role"
                                        isDisabled={!canChangeRole} optionFilter={roleFilter} />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="companyId">Company</label>
                                    <Field component={CompanySelect} name="companyId"
                                        isDisabled={!(isCreate && canSetCompany)}
                                    />
                                </div>
                            </div>
                            <button type="submit" className="btn btn-primary mr-2">Save</button>
                            <BackButton defaultPath="/users">Back</BackButton>
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
})(UserEditor);