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
        const {data}= await axios.get(`${process.env.REACT_APP_SPRING_URL}/users/winnumber`);
        return data.data;  
    } catch (error) {
        throw error.response.data;
    }
}

export { signin, signup, getWinNumber}