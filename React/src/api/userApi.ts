import { UnifiedResponse, SignupRequest, UserIfState } from "../shared/TypeMenu";
import { api } from "./config";

export type ChargingRequest = {
    msg: string,
    cash: number,
}

export const getInformation = async (): Promise<UnifiedResponse<UserIfState>> => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

export const logout = async (): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.post(`/users/logout`);
    return data;
}

export const getCashNickname = async (): Promise<UnifiedResponse<{cash: number, nickname: string}>> => {
    const { data } = await api.get("/users/cash");
    return data;
}

export const withdraw = async (msg: string): Promise<void> => {
    await api.patch("/users/withdraw", msg);
}

export const checkPW = async (msg: string): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.post("/users/check-pw", msg);
    return data;
}

export const update = async (inputValue: SignupRequest) => {
    const { data } = await api.patch("/users/update", inputValue);
    return data;
}

export const setCharges = async (inputValue: ChargingRequest) => {
    try {
        const { data } = await api.post("/users/charging", inputValue);
        return data.code;
    } catch (error) {
        throw error;
    }
}

export const getCharges = async (): Promise<UnifiedResponse<ChargingRequest[]>> => {
    try {
        const { data } = await api.get("/users/charging");
        return data;    
    } catch (error) {
        throw error;
    }
}

export const setPaid = async (msg: string): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch("/users/paid", msg);
        return data;
    } catch (error) {
        throw error; 
    }
}
    
export const getStatement = async (): Promise<UnifiedResponse<{localDate: string, msg: string}[]>> => {
    try {
        const { data } = await api.get("/users/statement");
        return data;
    } catch (error) {
        throw error;
    }
}