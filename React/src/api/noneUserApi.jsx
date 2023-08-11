import axios from "axios";
import { signApi } from "./config";

const signin = async (emailPassword) => {
    try {
        await signApi.post(`/signin`, emailPassword);    
    } catch (error) {
        throw error.response.data;
    }
}

const signup = async (inputValue) => {
    try {
        const response = await axios.post(`${process.env.REACT_APP_SPRING_URL}/users/signup`, inputValue);
        return response.data;
    } catch (error) {
        throw error.response.data;
    }
}

const getWinNumber = async () => {
    try {
        const res = await axios.get(`${process.env.REACT_APP_SPRING_URL}/winnumber`);
        return res.data;  
    } catch (error) {
        throw error.response.data;
    }
}

const checkLoginAndgetUserIf = async (tokens) => {
    try {
        const res = await axios.post(`${process.env.REACT_APP_SPRING_URL}/jwt/refresh/check`, {
            accessToken: tokens[0],
            refreshToken: tokens[1],
        }, {
            withCredentials: true,
        });
        return res.data;
    } catch (error) {
        if (error.response.data.message ===
            "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.") {
            const error = new Error("SignatureException");
            throw error.status(403);
        } else {
            throw error.response.data;
        }
    }
}

export { signin, signup, getWinNumber, checkLoginAndgetUserIf}