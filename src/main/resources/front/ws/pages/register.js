import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import styles from "@/styles/LoginRegistration.module.css"
import { backUrl, axiosInstance } from "@/components/pageContainer";
import { useRef, useState } from "react";
import ReCAPTCHA from "react-google-recaptcha";

const phoneRegExp = /^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$/
export default Register;


function Register() {
    const router = useRouter();
    const recaptchaRef = useRef();
    const [checked, setChecked] = useState(true);

    // form validation rules
    const validationSchema = Yup.object().shape({
        name: Yup.string()
            .required('First Name is required'),
        surname: Yup.string()
            .required('Last Name is required'),
        username: Yup.string()
            .required('Email is required').email(),
        password: Yup.string()
            .required('Password is required')
            .min(8, 'Password must be at least 8 characters').max(15, 'Password cant be longer than 15 characters').matches(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/,
                "Password must contain at least 8 characters, one uppercase, one number and one special case character"),
        confirmation: Yup.string().required().oneOf([Yup.ref('password'), null], "Passwords don't match."),
        phone: Yup.string().matches(phoneRegExp, 'Phone number is not valid')
    });
    const formOptions = { resolver: yupResolver(validationSchema) };

    // get functions to build form with useForm() hook
    const { register, handleSubmit, formState } = useForm(formOptions);
    const { errors } = formState;

    function onSubmit(formData) {
        const recaptchaValue = recaptchaRef.current.getValue();
        formData.emailValidation = checked;
        formData.recaptcha = recaptchaValue;
        axiosInstance.post(`${backUrl}/api/auth/register`, formData).then(resp => {
            router.replace('/login');
        }).catch(err => {
            alert(err.response.data);
        })
    }

    return (
        <div>
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Register</h1>
                <div className={styles.cardBody}>
                    <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
                        <div className={styles.formGroup}>
                            <label>First Name</label>
                            <input name="name" type="text" {...register('name')}
                                className={`${styles.formControl} ${errors.name ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.name && <div className={styles.invalidFeedback}>{errors.name?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>Last Name</label>
                            <input name="surname" type="text" {...register('surname')}
                                className={`${styles.formControl} ${errors.surname ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.surname && <div className={styles.invalidFeedback}>{errors.surname?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>E-mail</label>
                            <input name="username" type="text" {...register('username')}
                                className={`${styles.formControl} ${errors.username ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.username && <div className={styles.invalidFeedback}>{errors.username?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>Password</label>
                            <input name="password" type="password" {...register('password')}
                                className={`${styles.formControl} ${errors.password ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.password && <div className={styles.invalidFeedback}>{errors.password?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>Confirm Password</label>
                            <input name="confirmation" type="password" {...register('confirmation')}
                                className={`${styles.formControl} ${errors.confirmation ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.confirmation && <div className={styles.invalidFeedback}>{errors.confirmation?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>Phone</label>
                            <input name="phone" type="phone" {...register('phone')}
                                className={`${styles.formControl} ${errors.phone ? styles.isInvalid : ''}`} />

                        </div>
                        {errors.phone && <div className={styles.invalidFeedback}>{errors.phone?.message}</div>}
                        <div className={styles.checkBox}>
                            <label>Email validation</label>
                            <input name="emailValidation" checked={checked} value={checked}
                                type="radio" {...register('emailValidation')}
                                onChange={() => setChecked(true)} />
                        </div>
                        <div className={styles.checkBox}>
                            <label>Phone validation</label>
                            <input name="phoneValidation" checked={!checked} value={!checked} type="radio"
                                onChange={() => setChecked(false)} />
                        </div>
                        <ReCAPTCHA
                            ref={recaptchaRef}
                            sitekey="6LfEWIMmAAAAAG_1ZepVg757CP01pC-qakTTNByI"
                        />
                        <div className={styles.btnContainerRegistration}>
                            <div className={styles.noLinkContainer}>
                                <a href="/login" className={styles.noLink}>Have an account? Login</a>
                            </div>
                            <button disabled={formState.isSubmitting} className={styles.loginBtn}>
                                Register
                            </button>
                        </div>
                    </form>
                </div>
            </div >
        </div >
    );
}
