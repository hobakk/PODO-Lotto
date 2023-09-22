import axios from "axios";
import { dontLogin, api } from "./config";
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
        const response = await dontLogin.post("/users/signup", inputValue);
        return response.data;
    } catch (error: any) {
        throw error.response.data;
    }
}

const getWinNumber = async (): Promise<UnifiedResponse<{winNumberList: WinNumber[]}>> => {
    try {
        const res = await dontLogin.get(`/winnumber`);
        return res.data;  
    } catch (error: any) {
        throw error.response.data;
    }
}

const sendAuthCodeToEmail = async (email: string): Promise<UnifiedResponse<undefined>> => {
    try {
        const res = await dontLogin.post(`/users/email`, email);
        return res.data;  
    } catch (error: any) {
        throw error.response.data;
    }
}

export type EmailAuthCodeRequest = {
    email: string, 
    authCode: string,
}

const compareAuthCode = async (req: EmailAuthCodeRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const res = await dontLogin.post(`/users/email/auth-code`, req);
        return res.data;  
    } catch (error: any) {
        throw error.response.data;
    }
}

export { signin, signup, getWinNumber, sendAuthCodeToEmail, compareAuthCode }