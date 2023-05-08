import {useRouter} from "next/router";
import {useState} from "react";
import * as Yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {useForm} from "react-hook-form";
import {axiosInstance, backUrl} from "@/components/pageContainer";

export default ChangePw;

function ChangePw() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        username: Yup.string()
            .required('Email is required').email(),
    });
    const formOptions = {resolver: yupResolver(validationSchema)};

    // get functions to build form with useForm() hook
    const {register, handleSubmit, formState} = useForm(formOptions);
    const {errors} = formState;

    async function onSubmit(formData) {

        try {
            const response = await axiosInstance.get(`${backUrl}/api/auth/change`, {params: {username: formData.username}});
            console.log(response.status);
        } catch (e) {
            console.log(e);
        }


    }

    return (
        <div>
            <div className="card">
                <h4 className="card-header">Register</h4>
                <div className="card-body">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="form-group">
                            <label>E-mail</label>
                            <input name="username" type="text" {...register('username')}
                                   className={`form-control ${errors.username ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.username?.message}</div>
                        </div>
                        <button disabled={formState.isSubmitting} className="btn btn-primary">
                            {formState.isSubmitting && <span className="spinner-border spinner-border-sm mr-1"></span>}
                            Reset password
                        </button>
                        <a href="/login" className="btn btn-link">Cancel</a>
                    </form>
                </div>
            </div>

        </div>
    );
}