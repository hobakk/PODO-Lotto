import axios from "axios";
import React, { useEffect } from "react";
import { ACCESS_DENIED, DONT_LOGIN } from "../shared/TypeMenu";
import { useNavigate } from "react-router-dom";

const url = `${process.env.REACT_APP_SPRING_URL}`
const DONT_LOGIN = "DONT_LOGIN";
const ACCESS_DENIED ="ACCESS_DENIED";

const signApi = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080'	// 서버 domain, httpOnly 설정 때문에 추가
    },
    baseURL: `${url}/users`,
    withCredentials: true,
})

const api = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080',
    },
    baseURL: url,
    withCredentials: true,
})

api.interceptors.request.use(
    (config) => {
        config.headers['Content-Type'] = 'application/json';
        return config;
    },
    (error) => {
        console.log(error);
        return Promise.reject(error);
    }
)

api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        const result = error.response.data;
        console.log(result);
        if (result.exceptionType === DONT_LOGIN) {
            alert(result.msg);
            window.location.href = "/signin";
            return Promise.resolve();
        } else if (result.exceptionType === ACCESS_DENIED) {
            alert(result.msg);
            window.location.href = "/premium";
            return Promise.resolve();
        }

        return Promise.reject(result);
    }
)

export { signApi, api };