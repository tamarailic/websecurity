import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import ReCAPTCHA from "react-google-recaptcha";

import { backUrl, axiosInstance, getUserId } from "@/components/pageContainer";
import { setAuthTokens } from "axios-jwt";
import { useRef } from 'react';

export default Login;

function Login() {
    const router = useRouter();
    const recaptchaRef = useRef();

    // form validation rules
    const validationSchema = Yup.object().shape({
        username: Yup.string().required('Username is required'),
        password: Yup.string().required('Password is required')
    });
    const formOptions = { resolver: yupResolver(validationSchema) };

    // get functions to build form with useForm() hook
    const { register, handleSubmit, formState } = useForm(formOptions);
    const { errors } = formState;

    function onSubmit({ username, password }) {
        const recaptchaValue = recaptchaRef.current.getValue();
        axiosInstance.post(`${backUrl}/api/auth/login`,
            {
                email: username,
                password: password,
                recaptcha: recaptchaValue
            }).then(resp => {
                if (resp.status.isError) {
                    alert("Error in request");
                } else {
                    setAuthTokens({
                        accessToken: resp.data.accessToken,
                        refreshToken: resp.data.refreshToken
                    });
                }
            }).catch(err => {
                alert("Error in request");
            });
        recaptchaRef.current.reset();
    }

    return (
        <div>
            <div className="card">
                <h4 className="card-header">Login</h4>
                <div className="card-body">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="form-group">
                            <label>Username</label>
                            <input name="username" type="text" {...register('username')}
                                className={`form-control ${errors.username ? 'is-invalid' : ''}`} />
                            <div className="invalid-feedback">{errors.username?.message}</div>
                        </div>
                        <div className="form-group">
                            <label>Password</label>
                            <input name="password" type="password" {...register('password')}
                                className={`form-control ${errors.password ? 'is-invalid' : ''}`} />
                            <div className="invalid-feedback">{errors.password?.message}</div>
                        </div>
                        <ReCAPTCHA
                            ref={recaptchaRef}
                            sitekey="6LfEWIMmAAAAAG_1ZepVg757CP01pC-qakTTNByI"
                        />
                        <button disabled={formState.isSubmitting} className="btn btn-primary">
                            {formState.isSubmitting && <span className="spinner-border spinner-border-sm mr-1"></span>}
                            Login
                        </button>
                        <a href="/register" className="btn btn-link">Register</a>
                        <a href="/forgot-password" className="btn btn-link">Forgot Password</a>
                    </form>
                </div>
            </div>
        </div>
    );
}
