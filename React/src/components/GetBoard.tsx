import React, { useEffect, useState } from 'react'
import { BoardResponse, getBoard } from '../api/boardApi';
import { Err, UnifiedResponse } from '../shared/TypeMenu';
import { useMutation } from 'react-query';
import { CommonStyle, CommentStyle } from '../shared/Styles';
import { CommentRequest, setComment } from '../api/commentApi';

export const GetBoard = ({boardId}: {boardId: number}) => {
    const [msg, setMsg] = useState<string>("");
    const [value, setValue] = useState<BoardResponse>({
        boardId: -1,
        userName: "",
        subject: "",
        contents: "",
        status: "",
        commentList: [],
        correctionDate: ""
    });

    const getBoardMutation = useMutation<UnifiedResponse<BoardResponse>, Err, number>(getBoard, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) setValue(res.data);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    });

    useEffect(()=>{
        if (boardId >= 0) getBoardMutation.mutate(boardId);
    }, [boardId])

    return (
        <div style={CommonStyle}>
            {value.boardId >= 0 && (
                <div style={{ width: "20cm"}}>
                    <div style={{ display:"flex" }}>
                        <div>
                            <span>주제:</span>
                            <span style={{ marginLeft:"10px"}} >{value.subject}</span>
                        </div>
                        <div style={{ marginLeft:"auto"}}>
                            <span>작성자:</span>
                            <span style={{ marginLeft:"10px"}} >{value.userName}</span>
                        </div>
                    </div>

                    <div style={{ display:"flex", marginTop:"20px" }}>
                        <span style={{ marginLeft:"auto", color:"red"}} >{value.status}</span>
                    </div>

                    <div style={{ display:"flex", marginTop:"1cm" }}>
                        <span>{value.contents}</span>
                    </div>

                    {value.commentList.length !== 0 && (
                        value.commentList.map(item=>{
                            return (
                                item.nickname === value.userName ? (
                                    <div style={{ marginLeft:"auto", ...CommentStyle, backgroundColor:"yellow"}}>
                                        <div style={{ display:"flex" }}>
                                            <span>작성자:</span>
                                            <span style={{ marginLeft:"10px"}}>{item.nickname}</span>
                                            <button 
                                                style={{ marginLeft:"auto"}}
                                                // onClick={}
                                            >
                                                수정
                                            </button>
                                        </div>
                                        <span>{item.message}</span>
                                    </div>
                                ):(
                                    <div style={{ marginLeft:"auto", ...CommentStyle, backgroundColor:"gray", color:"white"}}>
                                        <span>관리자</span>
                                        <span>{item.message}</span>
                                    </div>
                                )
                            )
                        })
                    )}
                </div>
            )}
        </div>
    )
}

export default GetBoard;