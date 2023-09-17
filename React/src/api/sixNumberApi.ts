import { UnifiedResponse, SixNumber } from "../shared/TypeMenu";
import { api } from "./config";

export const buyNumber = async (value: number): Promise<UnifiedResponse<string[]>> => {
    try {
        const { data } = await api.post("/sixnum", value);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export type repetitionAndNum = {
    value: number,
    repetition: number,
}

export const statisticalNumber = async (values: repetitionAndNum): Promise<UnifiedResponse<string[]>> => {
    try {
        const { data } = await api.post("/sixnum/repetition", values);
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const getRecentNumber = async (): Promise<UnifiedResponse<string[]>> => {
    try {
        const res = await api.get("/sixnum/recent")
        return res.data;
    } catch (error: any) {
        throw error.data;
    }
}