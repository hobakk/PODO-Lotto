import { ApiResponse, ItemResponse, ListResponse, SignupRequest, UserIfState } from "../shared/TypeMenu";
import { api } from "./config";

export type ChargingRequest = {
    msg: string,
    cash: number,
}

export const getInformation = async (): Promise<ItemResponse<UserIfState>> => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

export const logout = async (): Promise<ApiResponse> => {
    const { data } = await api.post(`/users/logout`);
    return data;
}

export const getCashNickname = async (): Promise<ItemResponse<{cash: number, nickname: string}>> => {
    const { data } = await api.get("/users/cash");
    return data;
}

export const withdraw = async (msg: string): Promise<void> => {
    await api.patch("/users/withdraw", msg);
}

export const checkPW = async (msg: string): Promise<ApiResponse> => {
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

export const getCharges = async (): Promise<ListResponse<ChargingRequest[]>> => {
    try {
        const { data } = await api.get("/users/charging");
        return data;    
    } catch (error) {
        throw error;
    }
}

export const setPaid = async (msg: string): Promise<ApiResponse> => {
    try {
        const { data } = await api.patch("/users/paid", msg);
        return data;
    } catch (error) {
        throw error; 
    }
}
    
export const getStatement = async (): Promise<ListResponse<{localDate: string, msg: string}[]>> => {
    try {
        const { data } = await api.get("/users/statement");
        return data;
    } catch (error) {
        throw error;
    }
}