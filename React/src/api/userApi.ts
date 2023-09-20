import { UnifiedResponse, UserDetailInfo } from "../shared/TypeMenu";
import { SignupRequest } from "./noneUserApi";
import { api } from "./config";

export type ChargingRequest = {
    msg: string,
    cash: number,
}

export type ChargeResponse = {
    msg: string,
    cash: number,
    date: string,
}

export type CashNicknameDto = {
    cash: number,
    nickname: string
}

export const getInformation = async (): Promise<UnifiedResponse<UserDetailInfo>> => {
    const { data } = await api.get(`/users/my-information`);
    return data;
}

export const logout = async (): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.post(`/users/logout`);
    return data;
}

export const getCashNickname = async (): Promise<UnifiedResponse<CashNicknameDto>> => {
    const { data } = await api.get("/users/cash");
    return data;
}

export const withdraw = async (msg: string): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch("/users/withdraw", msg);
    return data;
}

export const checkPW = async (msg: string): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/users/check-pw", msg);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const update = async (inputValue: SignupRequest): Promise<UnifiedResponse<undefined>>  => {
    try {
        const { data } = await api.patch("/users/update", inputValue);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const setCharges = async (inputValue: ChargingRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/users/charge", inputValue);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const getCharges = async (): Promise<UnifiedResponse<ChargeResponse>> => {
    try {
        const { data } = await api.get("/users/charge");
        return data;    
    } catch (error: any) {
        throw error.data;
    }
}

export const setPaid = async (msg: string): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch("/users/paid", msg);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}
    
export const getStatement = async (): Promise<UnifiedResponse<{localDate: string, msg: string}[]>> => {
    try {
        const { data } = await api.get("/users/statement");
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export type SixNumberResponse = {
    date: string,
    numberList: string[],
}

export const getBuySixNumberList = async (): Promise<UnifiedResponse<SixNumberResponse[]>> => {
    try {
        const { data } = await api.get("/users/sixnumber-list");
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const getUserIfAndRefreshToken = async (): Promise<UnifiedResponse<UserDetailInfo>> => {
    try {
        const res = await api.get("/users/oauth2/my-information");
        return res.data;
    } catch (error: any) {
        throw error.data;
    }
}