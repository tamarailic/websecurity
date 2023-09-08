import Head from "next/head";
import NavBar from "./navBar";
import {
    IAuthTokens,
    TokenRefreshRequest,
    applyAuthTokenInterceptor,
    getBrowserLocalStorage,
    getAccessToken,
    getRefreshToken
} from 'axios-jwt'
import axios from 'axios'
import jwtDecode from "jwt-decode";
import https from 'https';
import {useRouter} from 'next/router';


export const backUrl = 'https://localhost:8443';

export default function PageContainer({children}) {
    return (
        <>
            <Head>
                <link rel="preconnect" href="https://fonts.googleapis.com"/>
                <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin/>
                <link
                    href="https://fonts.googleapis.com/css2?family=Inter:wght@100;200;300;400;500;600;700;800;900&display=swap"
                    rel="stylesheet"/>
            </Head>
            <NavBar/>
            {children}
        </>
    );
}


// 1. Create an axios instance that you wish to apply the interceptor to
export const axiosInstance = axios.create({
    baseURL: backUrl,
    httpsAgent: new https.Agent({
        rejectUnauthorized: false,
    }),
})

// 2. Define token refresh function.
const requestRefresh = (refresh) => {
    // Notice that this is the global axios instance, not the axiosInstance!  <-- important
    return axios.post(`${backUrl}/api/auth/refreshToken`, {refresh})
        .then(response => ({accessToken: response.data.accessToken, refreshToken: response.data.refreshToken}));
};

export function getUserRoles() {
    return jwtDecode(getAccessToken()).role[0].name;
}

export function getUserId() {
    return jwtDecode(getAccessToken()).id;
}

export function getUsername() {
    return jwtDecode(getAccessToken()).sub;
}

function isJwtExpired() {
    let token = getRefreshToken();
    if (token) {

        let decodedToken = jwtDecode(token);
        if (decodedToken) {
            const expiryDate = new Date(decodedToken.exp * 1000);
            if (expiryDate < new Date()) {
                localStorage.clear();
                window.location = "/login";
            }

        }
    }
}

axiosInstance.interceptors.request.use(
    function (config) {

        isJwtExpired();
        return config;
    },
);


applyAuthTokenInterceptor(axiosInstance, {requestRefresh});  // Notice that this uses the axiosInstance instance.  <-- important

