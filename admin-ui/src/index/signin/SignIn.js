import { Field, Form, Formik } from 'formik'
import { connect } from "react-redux"
import { Link, useHistory } from "react-router-dom"
import * as Yup from 'yup'
import HighlightInput from '../../common/HighlightInput'
import { onLogin } from '../../common/security/authReducer'
import authService from '../../common/security/authService'
import { onError } from '../../common/toastNotification'
import ToastContainer from '../../common/ToastContainer'
import './SignIn.css'

function SignIn({ onLogin, onError }) {
    const history = useHistory()

    const intialValues = {
        username: 'admin@acme.com',
        //username: 'petrov@company111.com',
        password: 'qwe123'
    }

    const validationSchema = Yup.object({
        username: Yup.string().email('Invalid email address').required('Required'),
        password: Yup.string().required('Required')
    })

    function onSubmit(values) {
        authService.login(values)
            .then(response => {
                onLogin(response.data)
                history.push("/")
            }, error => {
                onError("Bad credentials")
            })

    }

    return (<>
        <Formik
            initialValues={intialValues}
            validationSchema={validationSchema}
            onSubmit={onSubmit}>
            <div className="form-signin text-center">
                <Form className="w-100" noValidate={true}>
                    <Link to="/weclome">
                        <img className="mb-4" src="acme-icon.png" alt="" width="60" height="60" />
                    </Link>
                    <h1 className="h3 mb-3 fw-normal">Please sign in</h1>

                    <div className="form-floating">
                        <Field component={HighlightInput} name="username" type="email"
                            className="form-control" placeholder="Email address" />
                    </div>
                    <div className="form-floating mt-2">
                        <Field component={HighlightInput} name="password" type="password"
                            className="form-control" placeholder="Password" />
                    </div>

                    <button className="w-100 btn btn-primary mt-4" type="submit">Sign in</button>
                    <p className="mt-5 mb-3 text-muted">&copy {new Date().getFullYear()}</p>
                </Form>
            </div>
        </Formik>
        <ToastContainer />
    </>)
}

const mapStateToProps = () => ({})

export default connect(mapStateToProps, {
    onLogin, onError
})(SignIn)
