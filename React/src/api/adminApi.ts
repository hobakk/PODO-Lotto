import { AdminGetCharges, UnifiedResponse, UserAllIf, WinNumber, upDownCashRequest } from "../shared/TypeMenu";
import { api } from "./config";

export const getUsers = async (): Promise<UnifiedResponse<UserAllIf[]>> => {
    const { data } = await api.get("/admin/users");
    return data;
}

export const getAdminCharges = async (): Promise<UnifiedResponse<AdminGetCharges[]>> => {
    const { data } = await api.get("/admin/charges");
    return data;
}

export const getSearch = async ({ msg, cash }: {msg: string, cash: number}): Promise<UnifiedResponse<AdminGetCharges>> => {
    const { data } = await api.get("/admin/search", { params: {
        msg,
        cash,
    }});
    return data;
}

export const setAdmin = async ({ userId, msg }: {userId: number, msg: string}): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch(`/admin/users/${userId}`, msg);
    return data;
}

export const upCash = async (inputValue: upDownCashRequest): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch("/admin/users/up-cash", inputValue);
    return data;
}

export const downCash = async (inputValue: upDownCashRequest): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch("/admin/users/down-cash", inputValue);
    return data;
}

export const createLotto = async (): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.post("/admin/lotto");
    return data;
}

export const setStatusFromAdmin = async ({ userId, msg }: {userId: number, msg: string}): Promise<UnifiedResponse<undefined>> => {
    const { data } = await api.patch(`/admin/status/${userId}`, msg);
    return data;
}

export const setRoleFromAdmin = async ({ userId, msg }: {userId: number, msg: string}): Promise<UnifiedResponse<undefined>>  => {
    try {
        const { data } = await api.patch(`/admin/role/${userId}`, msg);
        return data;
    } catch (error) {
        throw error;
    }
}

export const setWinNumber = async (inputValue: WinNumber): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/winnumber/set", inputValue);
        return data;
    } catch (error) {
        throw error;
    }
}