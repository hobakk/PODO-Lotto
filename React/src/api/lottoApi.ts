import { UnifiedResponse } from "../shared/TypeMenu";
import { api } from "./config";

type LottoResponse = {
    countList: number[],
    value: string,
}

export const getMainTopNumber = async (): Promise<UnifiedResponse<LottoResponse>> => {
    try {
        const { data } = await api.get("/lotto/main");
        return data;
    } catch (error) {
        throw error;
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
    } catch (error) {
        throw error;
    }
}

export const getAllMonthStats = async (): Promise<UnifiedResponse<{yearMonthList: string[]}>> => {
    try {
        const { data } = await api.get("/lotto/yearMonth/all");
        return data;   
    } catch (error) {
        throw error;
    }
}