import { Field, Form, Formik } from 'formik'
import { Link } from "react-router-dom"
import { connect } from "react-redux"
import * as Yup from 'yup'
import HighlightInput from '../../common/HighlightInput'
import CountrySelect from '../../common/rf-data/CountrySelect'
import companyService from '../../company/companyService'
import './SignUp.css'
import { getErrorMessage} from '../../common/utils'
import { onSuccess, onError } from '../../common/toastNotification'
import ToastContainer from '../../common/ToastContainer'

const TEST_DATA = {
    fullName: 'Company 111',
    vatin: 'BY1111111',
    regNumber: 'REG1111111',
    country: 'BY',
    city: 'Minsk',
    address: 'Dzerginskogo str., ap. 10',
    site: 'company111.com',
    companyEmail: 'email@company111.com',
    companyPhone: '+375291111111',
    firstName: 'Petr',
    lastName: 'Petrov',
    email: 'petrov@company111.com',
    phone: '+375291111112',
    password: 'qwe123',
    confirmPassword: 'qwe123',
}

function SignUp({ onSuccess, onError }) {
    
    const intialValues = false ? TEST_DATA : {
        fullName: '',
        vatin: '',
        regNumber: '',
        country: '',
        city: '',
        address: '',
        site: '',
        companyEmail: '',
        companyPhone: '',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: ''
    }

    const validationSchema = Yup.object({
        fullName: Yup.string().required('Required'),
        vatin: Yup.string().required('Required'),
        regNumber: '',
        country: Yup.string().required('Required'),
        city: Yup.string().required('Required'),
        address: Yup.string().required('Required'),
        companyEmail: Yup.string().email('Invalid email address'),
        companyPhone: Yup.string().required('Required'),
        firstName: Yup.string().required('Required'),
        lastName: Yup.string().required('Required'),
        email: Yup.string().email('Invalid email address').required('Required'),
        phone: '',
        password: Yup.string().min(6, 'Must be 6 characters or longer').required('Required'),
        confirmPassword: Yup.string()
            .min(6, 'Must be 6 characters or longer')
            .required('Required')
            .test('passwords-match', 'Passwords must match', function (value) {
                return this.parent.password === value
            })
    })

    function onSubmit(formData, actions) {
        const request = {
            fullName: formData.fullName,
            email: formData.companyEmail,
            country: formData.country,
            city: formData.city,
            address: formData.address,
            vatin: formData.vatin,
            regNumber: formData.regNumber,
            site: formData.site,
            phone: formData.companyPhone,
            owner: {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                phone: formData.phone,
                password: formData.password,
                confirmPassword: formData.confirmPassword
            }
        }
        companyService.register(request)
            .then(() => {
                if (actions.resetForm) {
                    actions.resetForm()
                }
                onSuccess('Company request was sent successfuly. Check your email for next steps')
            }, error => onError(getErrorMessage(error.response.data)))
    }

    return (
        <Formik
            initialValues={intialValues}
            validationSchema={validationSchema}
            onSubmit={onSubmit}>
            <div className="signup-wrapper">
                <div className="signup-content rounded">
                    <div className="d-flex">
                        <Link to="/weclome">
                            <img className="mb-4" src="acme-icon.png" alt="" width="40" height="40" />
                        </Link>
                        <h2 className="h2 ml-2">Company registration</h2>
                    </div>

                    <Form noValidate={true}>
                        <h4 className="h4 mb-3">Company:</h4>
                        <div className="form-group">
                            <label htmlFor="fullName">Full name</label>
                            <Field component={HighlightInput} name="fullName"
                                type="text" className="form-control" />
                        </div>
                        <div className="form-row">
                            <div className="form-group col-md-6">
                                <label htmlFor="vatin">VAT identification number</label>
                                <Field component={HighlightInput} name="vatin"
                                    type="text" className="form-control" />
                            </div>
                            <div className="form-group col-md-6">
                                <label htmlFor="regNumber">Registration number</label>
                                <Field component={HighlightInput} name="regNumber"
                                    type="text" className="form-control" />
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-group col-md-6">
                                <label htmlFor="country">Country</label>
                                <Field component={CountrySelect} name="country"
                                    type="text" />
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
                                <Field component={HighlightInput} name="companyEmail"
                                    type="text" className="form-control" />
                            </div>
                            <div className="form-group col-md-4">
                                <label htmlFor="companyPhone">Phone</label>
                                <Field component={HighlightInput} name="companyPhone"
                                    type="text" className="form-control" />
                            </div>
                            <div className="form-group col-md-4">
                                <label htmlFor="site">Site</label>
                                <Field component={HighlightInput} name="site"
                                    type="text" className="form-control" />
                            </div>
                        </div>
                        <h4 className="h4 mb-3">Owner:</h4>
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
                                    type="text" className="form-control" />
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
                        <button type="submit" className="btn btn-primary">Submit</button>
                    </Form>

                    <ToastContainer />
                </div>
            </div>
        </Formik>
    )
}

const mapStateToProps = () => ({})

export default connect(mapStateToProps, {
    onSuccess, onError
})(SignUp)