import React, { useEffect, useState } from 'react'
import { BoardResponse, getBoard } from '../api/boardApi';
import { Err, UnifiedResponse } from '../shared/TypeMenu';
import { useMutation } from 'react-query';
import { CommonStyle, CommentStyle } from '../shared/Styles';
import { CommentRequest, fixComment, setComment } from '../api/commentApi';

export const GetBoard = ({boardId}: {boardId: number}) => {
    const [msg, setMsg] = useState<string>("");
    const [click, isClick] = useState<{[key: number]: boolean}>({});
    const [fixMsg, setFixMsg] = useState<{[key: string]: string}>({});
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

    const setCommentMutation = useMutation<UnifiedResponse<undefined>, Err, CommentRequest>(setComment, {
        onSuccess: (res)=>{
            if (res.code === 200 ) getBoardMutation.mutate(boardId);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    });

    const fixCommentMutation = useMutation<UnifiedResponse<undefined>, Err, CommentRequest>(fixComment, {
        onSuccess: (res)=>{
            if (res.code === 200 ) getBoardMutation.mutate(boardId);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    });

    useEffect(()=>{
        if (boardId >= 0) getBoardMutation.mutate(boardId);
    }, [boardId])

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setMsg(e.target.value);
    }

    const fixMsgHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;

        setFixMsg({
            ...fixMsg,
            [name]: value
        });
    }

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (msg === "" && value.boardId < 0) alert("댓글을 남길 수 없습니다.");
        else setCommentMutation.mutate({id: value.boardId, message: msg});
    }

    const onClickHandler = (index: number, commentId: number) => {
        isClick({
            ...click,
            [index]: click[index] === null ? (false):(!click[index])
        });

        if (click[index]) fixCommentMutation.mutate({id: commentId, message: fixMsg[`msg${index}`]})
    }

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
                        value.commentList.map((item, index)=>{
                            return (
                                item.nickname === value.userName ? (
                                    <div
                                        key={`${item.nickname}-${index}`}
                                        style={{ marginLeft:"auto", ...CommentStyle, backgroundColor:"yellow"}}
                                    >
                                        <div style={{ display:"flex" }}>
                                            <span>작성자:</span>
                                            <span style={{ marginLeft:"10px"}}>{item.nickname}</span>
                                            <button 
                                                style={{ marginLeft:"auto"}}
                                                onClick={()=>onClickHandler(index, item.commentId)}
                                            >
                                                {click[index] ? ("완료"):("수정")}
                                            </button>
                                        </div>
                                        {click[index] ? (
                                            <input 
                                                value={fixMsg[index]}
                                                type='text'
                                                name={`msg${index}`}
                                                onChange={fixMsgHandler}
                                            />
                                        ):(
                                            <span>{item.message}</span>
                                        )}
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
                    
                    <form 
                        onSubmit={onSubmitHandler}
                        style={{ display:"flex", marginTop:"2cm", justifyContent: "center", alignItems: "center"}}
                    >
                        <input 
                            style={{ width:"14cm", height:"2cm", padding:"5px", marginTop:"0.7cm"}}
                            value={msg}
                            type="text"
                            onChange={onChangeHandler}
                        />
                        <button style={{ width:"1.7cm", height:"1.6cm"}}>남기기</button>
                    </form>
                </div>
            )}
        </div>
    )
}

export default GetBoard;