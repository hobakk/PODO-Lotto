import { UnifiedResponse } from "../shared/TypeMenu";
import { CommentResponse } from "./commentApi";
import { api } from "./config";

export type FixBoardRequest = {
    id: number,
    request: BoardRequest
}

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

export type BoardsResponse = {
    boardId: number,
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

export const getBoardsByStatus = async (status: string): Promise<UnifiedResponse<BoardsResponse[]>> => {
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

export const fixBoard = async (req: FixBoardRequest): Promise<UnifiedResponse<undefined>> => {
    try {
        const { data } = await api.patch(`/board/${req.id}`, req.request);
        return data;  
    } catch (error: any) {
        throw error.data;
    }
}

export type PageBoardsRes = {
    content: BoardsResponse,
    totalPages: number,
    pageNumber: number,
    totalElements: number
}

export const getAllBoardsByStatus = async (status: string): Promise<UnifiedResponse<PageBoardsRes>> => {
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