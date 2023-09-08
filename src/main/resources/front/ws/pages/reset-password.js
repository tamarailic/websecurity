import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import styles from "@/styles/LoginRegistration.module.css"
import { backUrl, axiosInstance } from "@/components/pageContainer";
import Link from 'next/link';

export default Register;


function Register() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        code: Yup.string()
            .required('Code is required'),
        password: Yup.string()
            .required('Password is required')
            .min(8, 'Password must be at least 8 characters').max(15, 'Password cant be longer than 15 characters').matches(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/,
                "Password must contain at least 8 characters, one uppercase, one number and one special case character"),
        confirmation: Yup.string().required().oneOf([Yup.ref('password'), null], "Passwords don't match."),
    });
    const formOptions = { resolver: yupResolver(validationSchema) };

    // get functions to build form with useForm() hook
    const { register, handleSubmit, formState } = useForm(formOptions);
    const { errors } = formState;

    function onSubmit(formData) {
        axiosInstance.post(`${backUrl}/api/auth/change`, formData).then(resp => {
            if (resp.status.isError) {
                alert("Error in request");
            } else {
                router.push("/reset-password");
            }
        }).catch(err => {
            alert(err.response.data);
        });
    }

    return (
        <div>
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Reset password</h1>
                <div className={styles.cardBody}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className={styles.formGroup}>
                            <label>Code</label>
                            <input name="code" type="text" {...register('code')}
                                className={`${styles.formControl} ${errors.code ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.code && <div className={styles.invalidFeedback}>{errors.code?.message}</div>}
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
                        <div className={styles.btnContainerRegistration}>
                            <div className={styles.noLinkContainer}>
                                <Link href="/login" className={styles.noLink}>Back to login</Link>
                            </div>
                            <button disabled={formState.isSubmitting} className={styles.loginBtn}>
                                Reset password
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    );
}
