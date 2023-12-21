import { UnifiedResponse } from "../shared/TypeMenu";
import { CommentResponse } from "./commentApi";
import { api } from "./config";

export type BoardRequest = {
    subject: string,
    contents: string
}

export const setBoard = async (value: BoardRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/board", value);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}