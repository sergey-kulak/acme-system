import { useState, useEffect } from 'react';
import { Redirect, useHistory, useLocation } from 'react-router';
import authService from '../common/security/authService';
import { onLogin } from '../common/security/authReducer';
import { connect } from 'react-redux';

function Login({ onLogin }) {
    const history = useHistory();
    const query = new URLSearchParams(useLocation().search);
    const [code] = useState(query.get('code'));

    useEffect(() => {
        if (code) {
            authService.login({ code })
                .then(response => {
                    onLogin(response.data);
                    history.push("/my-order");
                })
        }
    }, [code, history, onLogin])

    return code ? 'Log in...' : <Redirect to="/access-denied" />;
}

export default connect(() => ({}), {
    onLogin
})(Login);