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
        const res = await signApi.post("/jwt/refresh/check", tokens);
        return res.data;
    } catch (error) {
        throw error.response.data;
    }
}

export { signin, signup, getWinNumber, checkLoginAndgetUserIf}