import React, { useEffect, useState } from 'react'
import { BoardResponse, getBoard, FixBoardRequest, fixBoard, BoardRequest, deleteBoard } from '../api/boardApi';
import { Err, UnifiedResponse } from '../shared/TypeMenu';
import { useMutation } from 'react-query';
import { CommonStyle, CommentStyle } from '../shared/Styles';
import { CommentRequest, deleteComment, fixComment, setComment } from '../api/commentApi';
import { useNavigate } from 'react-router-dom';
import useTextareaWithShiftEnter from '../hooks/useTextareaWithShiftEnter';

export const GetBoard = ({boardId}: {boardId: number}) => {
    const navigate = useNavigate();
    const [msg, setMsg] = useState<string>("");
    const [click, isClick] = useState<{[key: string]: boolean}>({});
    const [fixMsg, setFixMsg] = useState<{[key: string]: string}>({});
    const [isFixBoard, setFixBoard] = useState<boolean>(false);
    const [delBoard, setDelBoard] = useState<boolean>(false);
    const [value, setValue] = useState<BoardResponse>({
        boardId: -1,
        userName: "",
        subject: "",
        contents: "",
        status: "",
        commentList: [],
        correctionDate: ""
    });
    const [subject, setSubject] = useState<string>('');
    const { textValue, setTextValue, handleKeyDown } = useTextareaWithShiftEnter();

    const getBoardMutation = useMutation<UnifiedResponse<BoardResponse>, Err, number>(getBoard, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                setValue(res.data);
            } 
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

    const deleteCommentMutation = useMutation<UnifiedResponse<undefined>, Err, number>(deleteComment, {
        onSuccess: (res)=>{
            if (res.code === 200 ) getBoardMutation.mutate(boardId);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    });

    const fixBoardMutation = useMutation<UnifiedResponse<undefined>, Err, FixBoardRequest>(fixBoard, {
        onSuccess: (res)=>{
            if (res.code === 200 ) getBoardMutation.mutate(boardId);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    })

    const deleteBoardMuatation = useMutation<UnifiedResponse<undefined>, Err, number>(deleteBoard, {
        onSuccess: (res)=>{
            if (res.code === 200 ) {
                alert(res.msg);
                navigate("/");
            }
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

    const onClickHandler = (index: string, commentId: number) => {
        isClick({
            ...click,
            [index]: click[index] === null ? (false):(!click[index])
        });

        if (index.startsWith("fix") && click[index]) {
            fixCommentMutation.mutate({id: commentId, message: fixMsg[index]});
        } else if (index.startsWith("delete") && click[index]) {
            deleteCommentMutation.mutate(commentId);
        }
    }

    const fixBoardHandler = () => {
        if (isFixBoard) {
            if (subject !== "" || textValue !== "") {
                const req: FixBoardRequest = {
                    id: value.boardId,
                    request: {subject: subject, contents: textValue}
                }

                fixBoardMutation.mutate(req);
            }
        }

        setFixBoard(!isFixBoard);
    }

    const delBoardHandler = () => {
        if (delBoard) deleteBoardMuatation.mutate(value.boardId);

        setDelBoard(!delBoard);
    }

    return (
        <div style={CommonStyle}>
            {value.boardId >= 0 && (
                <div style={{ width: "20cm" }}>
                    <div style={{ display:"flex", marginTop:"20px", marginBottom:"10px" }}>
                        <button onClick={()=>{navigate("/boards/status")}}>뒤로가기</button>
                        <button 
                            style={{ marginLeft:"auto"}}
                            onClick={delBoardHandler}
                        >
                            {delBoard ? ("삭제 완료"):("문의 삭제")}
                        </button>
                        <button 
                            style={{ marginLeft:"5px"}}
                            onClick={fixBoardHandler}
                        >
                            {isFixBoard ? ("문의 수정완료"):("문의 수정하기")}
                        </button>
                    </div>
                    
                    <div style={{ display:"flex", border: "2px solid black", backgroundColor:"#D4F0F0", borderBottom:"0px" }}>
                        {isFixBoard ? (
                            <input 
                                value={subject}
                                type='text'
                                onChange={(e)=>{setSubject(e.target.value)}}
                            />
                        ):(
                            <h3>
                                <span style={{ marginLeft:"10px"}}>주제:</span>
                                <span style={{ marginLeft:"10px"}} >{value.subject}</span>
                            </h3>
                        )}
                    </div>

                    <div style={{ border: "2px solid black" }}>
                        <div style={{ display:"flex", marginTop:"20px" }}>
                            <span style={{ marginLeft:"10px"}}>작성자:</span>
                            <span>{value.userName}</span>
                            <span style={{ marginLeft:"auto", color:"red", marginRight:"10px"}} >{value.status}</span>
                        </div>

                        <div style={{ display:"flex", marginTop:"1cm", minHeight:"20vh", padding:"10px" }}>
                            {isFixBoard ? (
                                <textarea 
                                    style={{ width:"100%", height:"100px"}}
                                    value={textValue}
                                    onChange={(e)=>{setTextValue(e.target.value)}}
                                    onKeyDown={handleKeyDown}
                                />
                            ):(
                                <span white-space="pre-wrap">{value.contents}</span>
                            )}
                        </div>
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
                                                onClick={()=>onClickHandler(`fix${index}`, item.commentId)}
                                            >
                                                {click[`fix${index}`] ? ("완료"):("수정")}
                                            </button>
                                            <button 
                                                onClick={()=>onClickHandler(`delete${index}`, item.commentId)}
                                            >
                                                {click[`delete${index}`] ? ("확인"):("삭제")}
                                            </button>
                                        </div>
                                        {click[`fix${index}`] ? (
                                            <input 
                                                value={fixMsg[index]}
                                                type='text'
                                                name={`fix${index}`}
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
                            name="comment"
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