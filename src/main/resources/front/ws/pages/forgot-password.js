import { useRouter } from "next/router";
import { useState } from "react";
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import styles from "@/styles/LoginRegistration.module.css"
import { axiosInstance, backUrl } from "@/components/pageContainer";
import Link from "next/link";

export default ChangePw;

function ChangePw() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        username: Yup.string()
            .required('Email is required').email(),
    });
    const formOptions = { resolver: yupResolver(validationSchema) };

    // get functions to build form with useForm() hook
    const { register, handleSubmit, formState } = useForm(formOptions);
    const { errors } = formState;

    function onSubmit(formData) {
        axiosInstance.get(`${backUrl}/api/auth/change`, { params: { username: formData.username } }).then().catch();
    }

    return (
        <div>
            <div className={styles.card}>
                <h1 className={styles.cardHeader}>Forgot password</h1>
                <div className={styles.cardBody}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className={styles.formGroup}>
                            <label>E-mail</label>
                            <input name="username" type="text" {...register('username')}
                                className={`${styles.formControl} ${errors.username ? styles.isInvalid : ''}`} />
                        </div>
                        {errors.username && <div className={styles.invalidFeedback}>{errors.username?.message}</div>}
                        <div className={styles.btnContainerRegistration}>
                            <div className={styles.noLinkContainer}>
                                <Link href="/login" className={styles.noLink}>Cancel registration</Link>
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