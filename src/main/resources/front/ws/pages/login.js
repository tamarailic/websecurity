import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import styles from "@/styles/LoginRegistration.module.css"
import { backUrl, axiosInstance, getUserRoles } from "@/components/pageContainer";
import { setAuthTokens } from "axios-jwt";

export default Login;

function Login() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        username: Yup.string().required('Username is required'),
        password: Yup.string().required('Password is required')
    });
    const formOptions = { resolver: yupResolver(validationSchema) };

    // get functions to build form with useForm() hook
    const { register, handleSubmit, formState } = useForm(formOptions);
    const { errors } = formState;

    async function onSubmit({ username, password }) {
        const response = await axiosInstance.post(`${backUrl}/api/auth/login`,
            {
                email: username,
                password: password
            }).then(resp => {
                if (resp.status.isError) {
                    alert("Error in request")
                } else {
                    setAuthTokens({
                        accessToken: resp.data.accessToken,
                        refreshToken: resp.data.refreshToken
                    });
                }
                console.log(getUserRoles());
            });

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
            </div>
        </div>
    );
}
