import { UnifiedResponse, SixNumber } from "../shared/TypeMenu";
import { api } from "./config";

export const buyNumber = async (value: number): Promise<UnifiedResponse<string[]>> => {
    try {
        const { data } = await api.post("/sixnum", value);
        return data;
    } catch (error) {
        throw error;
    }
}

export const statisticalNumber = async (values: {value: number, repetition: number})
: Promise<UnifiedResponse<string[]>> => {
    try {
        const { data } = await api.post("/sixnum/repetition", values);
        return data.data;
    } catch (error) {
        throw error;
    }
}

export const getRecentNumber = async (): Promise<UnifiedResponse<SixNumber>> => {
    try {
        const res = await api.get("/sixnum/recent")
        return res.data;
    } catch (error) {
        throw error;
    }
}