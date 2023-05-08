import {useRouter} from 'next/router';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as Yup from 'yup';

import {backUrl, axiosInstance, getUserRoles} from "@/components/pageContainer";
import {setAuthTokens} from "axios-jwt";

export default Login;

function Login() {
    const router = useRouter();

    // form validation rules
    const validationSchema = Yup.object().shape({
        username: Yup.string().required('Username is required'),
        password: Yup.string().required('Password is required')
    });
    const formOptions = {resolver: yupResolver(validationSchema)};

    // get functions to build form with useForm() hook
    const {register, handleSubmit, formState} = useForm(formOptions);
    const {errors} = formState;

    async function onSubmit({username, password}) {
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
            <div className="card">
                <h4 className="card-header">Login</h4>
                <div className="card-body">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="form-group">
                            <label>Username</label>
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
                        <button disabled={formState.isSubmitting} className="btn btn-primary">
                            {formState.isSubmitting && <span className="spinner-border spinner-border-sm mr-1"></span>}
                            Login
                        </button>
                        <a href="/register" className="btn btn-link">Register</a>
                        <a href="/forgot-password" className="btn btn-link">Forgot Password</a>
                    </form>
                </div>
            </div>
        </div>
    );
}
