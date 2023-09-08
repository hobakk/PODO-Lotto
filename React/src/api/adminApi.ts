import { UnifiedResponse, UserDetailInfo, upDownCashRequest } from "../shared/TypeMenu";
import { api } from "./config";
import { ChargingDto } from "./userApi";

export const getUsers = async (): Promise<UnifiedResponse<UserDetailInfo[]>> => {
    const { data } = await api.get("/admin/users");
    return data;
}

export const getAdminCharges = async (): Promise<UnifiedResponse<upDownCashRequest[]>> => {
    const { data } = await api.get("/admin/charges");
    return data;
}

export const getSearch = async ( msgCash: ChargingDto ): Promise<UnifiedResponse<upDownCashRequest>> => {
    const { data } = await api.get("/admin/search", { params: {
        msg: msgCash.msg,
        cash: msgCash.cash,
    }});
    return data;
}

export const upCash = async (inputValue: upDownCashRequest): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch("/admin/users/up-cash", inputValue);
    return data;
}

export const downCash = async (inputValue: upDownCashRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch("/admin/users/down-cash", inputValue);
        return data;
    } catch (error) {
        throw error;
    }
}

export const createLotto = async (): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/admin/lotto");
        return data;    
    } catch (error) {
        throw error;
    }
}

export type UserIdMsgProps = {
    userId: number,
    msg: string,
}

export const setAdmin = async ({ userId, msg }: UserIdMsgProps): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch(`/admin/users/${userId}`, msg);
        return data;
    } catch (error) {
        throw error;
    }
}

export const setStatusFromAdmin = async ({ userId, msg }: UserIdMsgProps): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch(`/admin/status/${userId}`, msg);
        return data;
    } catch (error) {
        throw error;
    }
}

export const setRoleFromAdmin = async ({ userId, msg }: UserIdMsgProps): Promise<UnifiedResponse<undefined>>  => {
    try {
        const { data } = await api.patch(`/admin/role/${userId}`, msg);
        return data;
    } catch (error) {
        throw error;
    }
}

export type WinNumberRequest = {
    date: string,
    time: number,
    prize: number,
    winner: number,
    numbers: string,
}

export const setWinNumber = async (inputValue: WinNumberRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/winnumber/set", inputValue);
        return data;
    } catch (error) {
        throw error;
    }
}