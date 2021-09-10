import { Field, Form, Formik } from 'formik';
import { useEffect, useState, useRef } from "react";
import { connect } from 'react-redux';
import { useParams } from "react-router-dom";
import * as Yup from 'yup';
import BackButton from "../common/BackButton";
import userService from './userService';
import HighlightInput from '../common/HighlightInput';
import useHistoryBack from '../common/useHistoryBack';
import { onError, onSuccess } from '../common/toastNotification';
import UserRoleSelect from './UserRoleSelect';
import CompanySelect from '../company/CompanySelect';
import { hasRole, ROLE, getAllAccessibleRoles, hasExactRole } from "../common/security";
import { isEmpty } from "../common/utils";
import PublicPointSelect from '../public-point/PublicPointSelect';

const PP_ROLES = [ROLE.PP_MANAGER, ROLE.WAITER, ROLE.COOK];

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
        companyId: hasRole(auth, ROLE.ADMIN) ? '' : auth.user.cmpid,
        publicPointId: hasRole(auth, ROLE.COMPANY_OWNER) ? '' : auth.user.ppid,
    });
    const formikRef = useRef(null);
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
        companyId: Yup.string()
            .test('companyId-check', 'Required', function (value) {
                return this.parent.role === ROLE.ADMIN || this.parent.role === ROLE.ACCOUNTANT || !isEmpty(value);
            }),
        publicPointId: Yup.string()
            .test('publicPointId-check', 'Required', function (value) {
                let role = this.parent.role;
                return !PP_ROLES.includes(role) || !isEmpty(value);
            })
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
            userService.findById(id)
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
            publicPointId: user.publicPointId || '',
        });
    }

    function getFullName() {
        return `${user.lastName} ${user.firstName}`;
    }

    function onSubmit(formData) {
        let request = { ...formData };
        if (isCreate) {
            userService.create(request)
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
            userService.update(user.id, request)
                .then(() => {
                    onSuccess(`${getFullName()} user was updated successfuly`);
                    historyBack();
                }, error => {
                    let errorMessage = error.response.data.error;
                    onError(errorMessage || 'Error');
                })
        }
    }

    function getFormikValues() {
        return formikRef.current && formikRef.current.values;
    }

    function onCompanyChange(cmpId) {
        setFormData({
            ...getFormikValues(),
            companyId: cmpId,
            publicPointId: ''
        })
    }

    function onRoleChange(newRole) {
        let state = getFormikValues()
        setFormData({
            ...state,
            role: newRole,
            publicPointId: state.companyId && newRole
                && PP_ROLES.includes(newRole) ? state.publicPointId : ''
        })
    }

    function onPpChange(ppId) {
        setFormData({
            ...getFormikValues(),
            publicPointId: ppId
        });
    }

    function roleFilter(options) {
        let roles = getAllAccessibleRoles(auth);
        if (hasExactRole(auth, ROLE.PP_MANAGER) && auth.user.id !== id) {
            roles = roles.filter(role => role !== ROLE.PP_MANAGER);
        }
        return options.filter(opt => opt.value !== ROLE.ADMIN && roles.includes(opt.value))
    }

    const canChangeRole = hasRole(auth, ROLE.ADMIN, ROLE.COMPANY_OWNER, ROLE.PP_MANAGER) &&
        (isCreate || (user && user.role !== ROLE.ADMIN && auth.user.id !== id));
    const canSetCompany = hasRole(auth, ROLE.ADMIN);
    const isPpDisabled = !hasRole(auth, ROLE.COMPANY_OWNER) || !formData.role
        || !formData.companyId || !PP_ROLES.includes(formData.role)

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {user ? getFullName() : 'User creation'}
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
                                        onChange={onRoleChange}
                                        isDisabled={!canChangeRole} optionFilter={roleFilter} />
                                </div>
                                <div className="form-group col-md-6">
                                    <label htmlFor="companyId">Company</label>
                                    <Field component={CompanySelect} name="companyId"
                                        onChange={onCompanyChange} isClearable
                                        isDisabled={!(isCreate && canSetCompany)}
                                    />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="publicPointId">Public point</label>
                                    <Field component={PublicPointSelect} name="publicPointId"
                                        isDisabled={isPpDisabled} isClearable auth={auth}
                                        companyId={formData.companyId} onChange={onPpChange}
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