import {useRouter} from 'next/router';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import {backUrl, axiosInstance} from "@/components/pageContainer";
import {useState} from "react";

const phoneRegExp = /^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$/
export default Register;


function Register() {
    const router = useRouter();
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
    const formOptions = {resolver: yupResolver(validationSchema)};

    // get functions to build form with useForm() hook
    const {register, handleSubmit, formState} = useForm(formOptions);
    const {errors} = formState;

    async function onSubmit(name) {
        console.log({
            name: name,
        });
        const response = await axiosInstance.post(`${backUrl}/api/auth/register`, name

        );
        console.log(response);


    }

    return (
        <div>
            <div className="card">
                <h4 className="card-header">Register</h4>
                <div className="card-body">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="form-group">
                            <label>First Name</label>
                            <input name="name" type="text" {...register('name')}
                                   className={`form-control ${errors.name ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.name?.message}</div>
                        </div>
                        <div className="form-group">
                            <label>Last Name</label>
                            <input name="surname" type="text" {...register('surname')}
                                   className={`form-control ${errors.surname ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.surname?.message}</div>
                        </div>
                        <div className="form-group">
                            <label>E-mail</label>
                            <input name="username" type="text" {...register('username')}
                                   className={`form-control ${errors.username ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.username?.message}</div>
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
                        <div className="form-group">
                            <label>Phone</label>
                            <input name="phone" type="phone" {...register('phone')}
                                   className={`form-control ${errors.phone ? 'is-invalid' : ''}`}/>
                            <div className="invalid-feedback">{errors.phone?.message}</div>
                        </div>
                        <div className="form-group">
                            <input name="emailValidation" checked={checked} value={checked}
                                   type="radio" {...register('emailValidation')} className="form-control"
                                   onChange={() => setChecked(true)}/> Email validation
                            <input name="phoneValidation" checked={!checked} value={!checked} type="radio"
                                   className="form-control" onChange={() => setChecked(false)}/> Phone validation
                        </div>
                        <button disabled={formState.isSubmitting} className="btn btn-primary">
                            {formState.isSubmitting && <span className="spinner-border spinner-border-sm mr-1"></span>}
                            Register
                        </button>
                        <a href="/login" className="btn btn-link">Cancel</a>
                    </form>
                </div>
            </div>

        </div>
    );
}
