import axios from "axios";
import { getCookie } from "../shared/Cookie";

const url = `${process.env.REACT_APP_SPRING_URL}/users`;

const api = axios.create({
    baseURL: url,
    headers: {
        ignore: false,
    },
})

const signup = async (v) => {
    await api.post(`/signup`, v);
}

const signin = async (v) => {
    await axios.post(`${url}/signin`, v, {
        headers: {
            ignore: true,
        }, 
        withCredentials: true
    });

    const accessToken = getCookie("accessToken");
    if (accessToken !== null) {
        return accessToken;
    } else {
        alert("로그인을 해주세요")
        window.location.href = "/signin";
    }
}

const logout = async () => {
    await api.post(`/logout`, {withCredentials: true})
}

const getCash = async () => {
    return await api.get(`/cash`)
}
