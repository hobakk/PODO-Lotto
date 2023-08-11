import { Cookies } from "react-cookie";

const cookies = new Cookies();


export const getCookie = (name: string) => {
    return cookies.get(name);
}

export const deleteToken = () => {
    try {
        cookies.remove('accessToken');
        cookies.remove('refreshToken');
    } catch (error) {
        console.error('removing cookies:', error)
    }
}

export const getAllCookie = () => {
    if  (!cookies.get("accessToken") || !cookies.get("refreshToken")) {
        return null;
    }
    return cookies.get("accessToken") + "," + cookies.get("refreshToken");
}