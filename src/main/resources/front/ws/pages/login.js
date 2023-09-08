import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import ReCAPTCHA from "react-google-recaptcha";
import { backUrl, axiosInstance, getUserId } from "@/components/pageContainer";
import { getAccessToken, setAuthTokens } from "axios-jwt";
import { useRef } from 'react';
import styles from "@/styles/LoginRegistration.module.css"
import { useSession, signIn, signOut } from "next-auth/react"
import Image from 'next/image';
import Link from 'next/link';

export default Login;

function Login() {
    const router = useRouter();
    const recaptchaRef = useRef();

    const { data: session } = useSession();

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
                    localStorage.setItem('username', JSON.stringify(username));
                    localStorage.setItem('password', JSON.stringify(password));
                    router.push("/f2a");
                }
            }).catch(err => {
                try {
                    if (err.response.data)
                        if (err.response.data.message)
                            if (err.response.data.message.includes("expired")){
                                localStorage.setItem('username', JSON.stringify(username));

                                router.replace('/refresh-password');
                            }
                            else {
                                alert(err);
                            }
                }
                catch (e){
                    alert(err);
                }

            });
        recaptchaRef.current.reset();
    }

    if (session) {
        try {
            if (getAccessToken() == null) {
                axiosInstance.post(`${backUrl}/api/auth/creteOrLogin`, { 'username': session.user.email, 'fullName': session.user.name })
                    .then(resp => {
                        if (resp.status.isError) {
                            console.log(resp);
                        } else {
                            setAuthTokens({
                                accessToken: resp.data.accessToken,
                                refreshToken: resp.data.refreshToken
                            });
                        }
                    })
                    .catch(err => {
                        console.log(err);
                    });
            }
            router.push('/');
        } catch {
            ;
        }

    }

    return (
        <div>
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Welcome to websecurity</h1>
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
                        <div className={styles.recaptcha}>
                            <ReCAPTCHA
                                ref={recaptchaRef}
                                sitekey="6LfEWIMmAAAAAG_1ZepVg757CP01pC-qakTTNByI"
                            />
                        </div>

                        <div className={styles.btnContainerLogin}>
                            <button disabled={formState.isSubmitting} className={styles.loginBtn}>
                                Login
                            </button>
                        </div>

                        <div >
                            <p>Don't have account? <Link href="/register">Register</Link></p>
                        </div>
                        <div>
                            <p>Forgot password? <Link href="/forgot-password" >Reset password</Link></p>
                        </div>
                    </form>

                    <div className={styles.ouathContainer}>
                        <h3>OAuth</h3>
                        <div className={styles.git} onClick={() => signIn("github")}>
                            <p>GitHub</p>
                            <div>
                                <Image src='/images/github.png' alt='GitHub' width={30} height={30} />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
