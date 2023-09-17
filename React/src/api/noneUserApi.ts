import axios from "axios";
import { api } from "./config";
import { UnifiedResponse, WinNumber } from "../shared/TypeMenu";

export type SigninRequest = {
    email: string,
    password: string
}

export type SignupRequest = {
    email: string,
    password: string,
    nickname: string,
}

const signin = async (emailPassword: SigninRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const response = await api.post(`/users/signin`, emailPassword);    
        return response.data;
    } catch (error: any) {
        throw error.data;
    }
}

const signup = async (inputValue: SignupRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const response = await axios.post(`${process.env.REACT_APP_SPRING_URL}/users/signup`, inputValue);
        return response.data;
    } catch (error: any) {
        throw error.response.data;
    }
}

const getWinNumber = async (): Promise<UnifiedResponse<{winNumberList: WinNumber[]}>> => {
    try {
        const res = await axios.get(`${process.env.REACT_APP_SPRING_URL}/winnumber`);
        return res.data;  
    } catch (error: any) {
        throw error.response.data;
    }
}

export { signin, signup, getWinNumber, }