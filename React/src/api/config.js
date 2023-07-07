import axios from "axios";
import { getCookie } from "../shared/Cookie";

const url = `${process.env.REACT_APP_SPRING_URL}`

const signApi = axios.create({
    baseURL: `${url}/users`,
    withCredentials: true,
})

const api = axios.create({
    baseURL: url,
})

api.interceptors.request.use(
    (config) => {
        config.headers['Content-Type'] = 'application/json';
        config.headers['Authorization'] = `Bearer ${getCookie("accessToken")}`;
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
        if (error.response?.status === 401) {
            return error;
        }
    }
)

export { signApi, api };
