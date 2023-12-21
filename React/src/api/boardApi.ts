import { UnifiedResponse } from "../shared/TypeMenu";
import { CommentResponse } from "./commentApi";
import { api } from "./config";

export type BoardRequest = {
    subject: string,
    contents: string
}

export type BoardResponse = {
    boardId: number,
    userName: string,
    subject: string,
    contents: string,
    status: string,
    commentList: CommentResponse[],
    correctionDate: string
}

export const setBoard = async (value: BoardRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.post("/board", value);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}

export const getBoardsByStatus = async (status: string): Promise<UnifiedResponse<BoardResponse[]>> => {
    try {
        const { data } = await api.get("/board", {
            params: {
                status,
            }
        });
        return data;   
    } catch (error: any) {
        throw error.data;
    }
}

export const getBoard = async (boardId: number): Promise<UnifiedResponse<BoardResponse>> => {
    try {
        const { data } = await api.get(`/board/${boardId}`);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}

export const deleteBoard = async (boardId: number): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.delete(`/board/${boardId}`);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}

export const fixBoard = async (boardId: number, boardRequest: BoardRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch(`/board/${boardId}`, boardRequest);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}

export const getAllBoardsByStatus = async (status: string): Promise<UnifiedResponse<BoardResponse>> => {
    try {
        const { data } = await api.get("/board/admin", {
            params: {
                status,
            }
        });
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}