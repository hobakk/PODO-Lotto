import { UnifiedResponse } from "../shared/TypeMenu";
import { api } from "./config";

export const getTimeOfWinNumber = async (): Promise<UnifiedResponse<number>> => {
    try {
        const { data } = await api.get("/winnumber/first");
        return data;
    } catch (error: any) {
        throw error.data;
    }
}