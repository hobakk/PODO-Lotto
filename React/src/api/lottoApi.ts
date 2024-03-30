import { UnifiedResponse } from "../shared/TypeMenu";
import { api } from "./config";

export type LottoResponse = {
    countList: number[],
    value: string,
}

export type AllMonthProps = {
    yearMonthList: string[],
}

export const createLotto = async (): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/lotto/main/admin");
        return data;    
    } catch (error: any) {
        throw error.data;
    }
}

export const getMainTopNumber = async (): Promise<UnifiedResponse<LottoResponse>> => {
    try {
        const { data } = await api.get("/lotto/main");
        return data;
    } catch (error: any) {
        throw error.data;
    }
}

export const checkMain = async (): Promise<Boolean> => {
    const { data } = await api.get("/lotto/main/admin");
    return data;
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

export type MonthlyStatsReq = {
    year: number,
    month: number
}

export const createMonthlyStats = async (value: MonthlyStatsReq): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post(`/lotto/stats/${value.year}/${value.month}`);
        return data;   
    } catch (error: any) {
        throw error.data;
    }
}