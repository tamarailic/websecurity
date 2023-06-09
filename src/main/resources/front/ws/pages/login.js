import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import ReCAPTCHA from "react-google-recaptcha";
import { backUrl, axiosInstance, getUserId } from "@/components/pageContainer";
import { setAuthTokens } from "axios-jwt";
import { useRef } from 'react';
import styles from "@/styles/LoginRegistration.module.css"

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
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Welcome to websecurity,</h1>
                <p className={`${styles.cardHeader} ${styles.subtitle}`}>let us take care of your ceritificates</p>
                <div className={styles.cardBody}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className={styles.formGroup}>
                            <label>Username</label>
                            <input name="username" type="text" {...register('username')}
                                className={`${styles.formControl} ${errors.username ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.username && <div className={styles.invalidFeedback}>{errors.username?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>Password</label>
                            <input name="password" type="password" {...register('password')}
                                className={`${styles.formControl} ${errors.password ? styles.isInvalid : ''}`} />

                        </div>
                        {errors.username && <div className={styles.invalidFeedback}>{errors.password?.message}</div>}
                        <ReCAPTCHA
                            ref={recaptchaRef}
                            sitekey="6LfEWIMmAAAAAG_1ZepVg757CP01pC-qakTTNByI"
                        />
                        <div className={styles.btnContainerLogin}>
                            <button disabled={formState.isSubmitting} className={styles.loginBtn}>
                                Login
                            </button>
                        </div>

                        <div >
                            <p>Don't have account? <a href="/register">Register</a></p>
                        </div>
                        <div>
                            <p>Forgot password? <a href="/forgot-password" >Reset password</a></p>
                        </div>
                    </form>
                </div>
            </div >
        </div >
    );
}
