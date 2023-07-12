import { Cookies } from "react-cookie";

const cookies = new Cookies();


export const getCookie = (name) => {
    return cookies.get(name);
}

export const deleteToken = () => {
    cookies.remove('accessToken');
    cookies.remove('refreshToken');
}