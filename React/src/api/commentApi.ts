import { UnifiedResponse } from "../shared/TypeMenu";
import { api } from "./config";

export type CommentResponse = {
    nickname: string,
    message: string
}

export type CommentRequest = {
    id: number,
    message: string
}

export const setComment = async (req: CommentRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/comment", req);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}
