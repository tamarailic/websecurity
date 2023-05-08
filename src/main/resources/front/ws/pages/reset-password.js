import {useRouter} from 'next/router';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import {backUrl, axiosInstance} from "@/components/pageContainer";
import {useState} from "react";

const codeRegExp = /^[0-9]{7}$/
export default Register;


function Register() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        code: Yup.string()
            .required('Code is required').matches(codeRegExp),
        password: Yup.string()
            .required('Password is required')
            .min(8, 'Password must be at least 8 characters').max(15, 'Password cant be longer than 15 characters').matches(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/,
                "Password must contain at least 8 characters, one uppercase, one number and one special case character"),
        confirmation: Yup.string().required().oneOf([Yup.ref('password'), null], "Passwords don't match."),
    });
    const formOptions = {resolver: yupResolver(validationSchema)};

    // get functions to build form with useForm() hook
    const {register, handleSubmit, formState} = useForm(formOptions);
    const {errors} = formState;

    async function onSubmit(formData) {

        try {
            const response = await axiosInstance.post(`${backUrl}/api/auth/change`, formData);
            console.log(response.status);
        }
        catch (e){
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
                            <label>Code</label>
                            <input name="code" type="text" {...register('code')}
                                   className={`form-control ${errors.code ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.code?.message}</div>
                        </div>

                        <div className="form-group">
                            <label>Password</label>
                            <input name="password" type="password" {...register('password')}
                                   className={`form-control ${errors.password ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.password?.message}</div>
                        </div>
                        <div className="form-group">
                            <label>Confirm Password</label>
                            <input name="confirmation" type="password" {...register('confirmation')}
                                   className={`form-control ${errors.confirmation ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.confirmation?.message}</div>
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
