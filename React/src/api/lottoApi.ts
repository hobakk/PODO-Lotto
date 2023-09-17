import { UnifiedResponse } from "../shared/TypeMenu";
import { api } from "./config";

export type LottoResponse = {
    countList: number[],
    value: string,
}

export type AllMonthProps = {
    yearMonthList: string[],
}

export const getMainTopNumber = async (): Promise<UnifiedResponse<LottoResponse>> => {
    try {
        const { data } = await api.get("/lotto/main");
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const getTopNumberForMonth = async (yearMonth: string): Promise<UnifiedResponse<LottoResponse>> => {
    try {
        const { data } = await api.get("/lotto/yearMonth", {
            params: {
                yearMonth,
            }
        });
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const getAllMonthStats = async (): Promise<UnifiedResponse<AllMonthProps>> => {
    try {
        const { data } = await api.get("/lotto/yearMonth/all");
        return data;   
    } catch (error: any) {
        throw error.data;
    }
}