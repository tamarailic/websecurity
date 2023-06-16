import {useRouter} from 'next/router';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import styles from "@/styles/LoginRegistration.module.css"
import {backUrl, axiosInstance} from "@/components/pageContainer";
import Link from 'next/link';
import {setAuthTokens} from "axios-jwt";

export default Register;


function Register() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        code: Yup.string()
            .required('Code is required')
    });
    const formOptions = {resolver: yupResolver(validationSchema)};

    // get functions to build form with useForm() hook
    const {register, handleSubmit, formState} = useForm(formOptions);
    const {errors} = formState;

    function onSubmit(formData) {
        console.log(formData);
        let data = {
            email: localStorage.getItem("username").replaceAll("\"",""),
            password: localStorage.getItem("password").replaceAll("\"",""),
            code: formData.code
        }
        axiosInstance.post(`${backUrl}/api/auth/2fa`, data).then(resp => {
            if (resp.status == 401) {
                alert("Error in request");
            } else {
                setAuthTokens({
                    accessToken: resp.data.accessToken,
                    refreshToken: resp.data.refreshToken
                });
                localStorage.removeItem("username");
                localStorage.removeItem("password");
                router.replace('/');
            }
        }).catch(err => {
            alert(err);
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
                                   className={`${styles.formControl} ${errors.code ? styles.isInvalid : ''}`}/>
                        </div>
                        {errors.code && <div className={styles.invalidFeedback}>{errors.code?.message}</div>}
                        <div className={styles.btnContainerRegistration}>
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
