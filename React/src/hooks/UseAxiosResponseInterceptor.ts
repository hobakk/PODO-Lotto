import { useEffect } from "react"
import axios, { AxiosHeaders, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { api } from "../api/config"
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../config/configStore";
import { useNavigate } from "react-router-dom";
import { setRefreshToken } from "../modules/refreshTokenSlice";
import { persistor } from "../config/configStore";

const UesAxiosResponseInterceptor = () => {
    const refreshToken = useSelector((state: RootState)=>state.refreshToken.refreshToken);
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const requestHandler = (request: InternalAxiosRequestConfig<any>) => {
        request.headers['Content-Type'] = 'application/json';
        return request;
    };

    const responseHandler = (response: AxiosResponse<any, any>) => {
        const header = response.headers;
        if (header instanceof AxiosHeaders) {
            if (header.has('Authorization')) {
                const encodedRefresh: string = header.get('Authorization')?.toString().split(" ")[1] ?? "";
                if (encodedRefresh !== "") dispatch(setRefreshToken(encodedRefresh));
            } 
        }

        return response;
    }

    const errorHandler = async (error: any) => {
        if (error.response) {
            const { exceptionType, msg } = error.response.data;
            if (exceptionType === "RE_ISSUANCE") {
                const newConfig = error.response.config;
                newConfig.headers.set('Authorization', `Bearer ${refreshToken}`);
                return await axios.request(newConfig)
            } else if (exceptionType === "DONT_LOGIN") {
                alert(msg);
                navigate("/signin");
            } else if (exceptionType === "ACCESS_DENIED") {
                alert(msg);
                navigate("/premium");
            } else if (exceptionType === "ONLY_ADMIN_ACCESS_API") {
                alert(msg);
                navigate("/");
            } else if (exceptionType === "REFRESH_ISNULL") {
                alert("토큰이 만료되어 로그아웃 됩니다");
                await persistor.purge();
            }
        }
            
        return error;
    }

    const requestInterceptor = api.interceptors.request.use((request)=> requestHandler(request));

    const responseInterceptor = api.interceptors.response.use(
        (response) => responseHandler(response),
        (error) => errorHandler(error)
    );

    useEffect(() => {
        return () => {
            api.interceptors.request.eject(requestInterceptor)
            api.interceptors.response.eject(responseInterceptor)
        }
    }, [requestInterceptor, responseInterceptor])

    return null;
};

export default UesAxiosResponseInterceptor;