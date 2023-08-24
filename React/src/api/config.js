import axios from "axios";
import { getCookie } from "../shared/Cookie";

const url = `${process.env.REACT_APP_SPRING_URL}`

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
        return Promise.reject(result);
    }
)

export { signApi, api };
