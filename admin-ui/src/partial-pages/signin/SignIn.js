import { Field, Form, Formik } from 'formik';
import * as Yup from 'yup';
import { connect } from "react-redux";
import { Link, useHistory } from "react-router-dom";
import AuthService from '../../common/AuthService';
import HighlightInput from '../../common/HighlightInput';
import './SignIn.css';
import { onLogin } from '../../reducers/Auth';

function SignIn({ onLogin }) {
    const history = useHistory();

    const intialValues = {
        //username: 'admin@acme.com',
        username: 'petrov@company111.com',
        password: 'qwe123'
    };

    const validationSchema = Yup.object({
        username: Yup.string().email('Invalid email address').required('Required'),
        password: Yup.string().required('Required')
    });

    function onSubmit(values) {
        AuthService.login(values)
            .then(response => {
                onLogin(response.data);
                history.push("/");
            })

    }

    return (
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
                    <p className="mt-5 mb-3 text-muted">&copy; {new Date().getFullYear()}</p>
                </Form>
            </div>
        </Formik>

    )
}

export default connect(
    state => {
        return {}
    }, {
    onLogin
})(SignIn);
