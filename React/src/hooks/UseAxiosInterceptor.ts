import { useEffect } from "react"
import axios, { AxiosHeaders, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { api } from "../api/config"
import { useNavigate } from "react-router-dom";
import { persistor } from "../config/configStore";

const useAxiosResponseInterceptor = () => {
    const navigate = useNavigate();
    const purge = async () => { await persistor.purge(); }

    const requestHandler = (request: InternalAxiosRequestConfig<any>) => {
        request.headers['Content-Type'] = 'application/json';
        return request;
    };

    const responseHandler = (response: AxiosResponse<any, any>) => {
        return response;
    }

    const errorHandler = async (error: any) => {
        if (error.response) {
            const { exceptionType, msg } = error.response.data;
            if (exceptionType === "RE_ISSUANCE") {
                const newConfig = error.response.config;
            
                return await axios.request(newConfig)
            } else if (exceptionType === "DONT_LOGIN") {
                purge();
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

            return Promise.reject(error.response);
        }
            
        return Promise.reject(error);
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

export default useAxiosResponseInterceptor;