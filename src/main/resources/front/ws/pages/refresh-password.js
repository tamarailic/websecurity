import styles from "@/styles/LoginRegistration.module.css";
import Link from "next/link";
import {useRouter} from "next/router";
import * as Yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {useForm} from "react-hook-form";
import {axiosInstance, backUrl} from "@/components/pageContainer";


export default Refresh;

function Refresh() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        oldPassword: Yup.string()
            .required('Password is required')
            .min(8, 'Password must be at least 8 characters').max(15, 'Password cant be longer than 15 characters').matches(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/,
                "Password must contain at least 8 characters, one uppercase, one number and one special case character"),
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
        console.log(formData);
        let data = {
            username: localStorage.getItem("username").replaceAll("\"",""),
            oldPassword:formData.oldPassword,
            password:formData.password
        }
        axiosInstance.post(`${backUrl}/api/auth/refresh-password`, data).then(
            resp => {
                alert("Password successfully changed");
                router.push("/login")
            }
        ).catch();
        localStorage.removeItem("username");
    }
    return (
        <div>
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Reset password</h1>
                <div className={styles.cardBody}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className={styles.formGroup}>
                            <label>Old Password</label>
                            <input name="oldPassword" type="password" {...register('oldPassword')}
                                   className={`${styles.formControl} ${errors.oldPassword ? styles.isInvalid : ''}`}/>
                        </div>
                        {errors.oldPassword && <div className={styles.invalidFeedback}>{errors.errors.oldPassword?.message}</div>}
                        <div className={styles.formGroup}>
                            <label>New Password</label>
                            <input name="password" type="password" {...register('password')}
                                   className={`${styles.formControl} ${errors.password ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.username && <div className={styles.invalidFeedback}>{errors.password?.message}</div>}

                        <div className={styles.formGroup}>
                            <label>Confirm Password</label>
                            <input name="confirmation" type="password" {...register('confirmation')}
                                   className={`${styles.formControl} ${errors.confirmation ? styles.isInvalid : ''}`}/>
                        </div>
                        {errors.confirmation &&
                            <div className={styles.invalidFeedback}>{errors.confirmation?.message}</div>}
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