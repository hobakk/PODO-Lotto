import axios from "axios";
import { signApi } from "./config";
import { UnifiedResponse, Err, SignupRequest, UserIfState, WinNumber } from "../shared/TypeMenu";

const signin = async (emailPassword: {email: string, password: string}): Promise<void> => {
    try {
        await signApi.post(`/signin`, emailPassword);    
    } catch (error: any) {
        throw error.response.data;
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
        const err: Err = error.response.data;
        throw err;
    }
}

const checkLoginAndgetUserIf = async (tokens: string[]): Promise<UnifiedResponse<UserIfState>> => {
    try {
        const res = await axios.post(`${process.env.REACT_APP_SPRING_URL}/jwt/check/login`, {
            accessToken: tokens[0],
            refreshToken: tokens[1],
        }, {
            withCredentials: true,
        });
        console.log(res);
        return res.data;
    } catch (error: any) {
        if (error.response.data.message ===
            "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.") {
            throw new Error("SignatureException");
        } else {
            throw error.response.data;
        }
    }
}

export { signin, signup, getWinNumber, checkLoginAndgetUserIf}