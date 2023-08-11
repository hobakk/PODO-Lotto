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
    if  (!getCookie("accessToken") || !getCookie("refreshToken")) {
        return null;
    }
    
    return getCookie("accessToken") + "," + getCookie("refreshToken");
}

export const getAccessTAndRefreshT = (): [string | undefined, string | undefined] => {
    const tokens = getAllCookie()?.split(",");
    if (tokens?.length === 2) {
        return [tokens[0], tokens[1]];
    }
    
    return [undefined, undefined];
}