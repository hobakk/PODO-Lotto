import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, InputBox, TitleStyle, MsgAndInput, ButtonDiv, ButtonStyle } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { Err, UnifiedResponse } from '../../shared/TypeMenu'
import { BoardRequest, setBoard } from '../../api/boardApi'
import { useNavigate } from 'react-router-dom'
import useTextareaWithShiftEnter from '../../hooks/useTextareaWithShiftEnter'

function SetBoard() {
    const subjectRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const [subject, setSubject] = useState<string>("");
    const { textValue, setTextValue, handleKeyDown } = useTextareaWithShiftEnter();

    const setBoardMutation = useMutation<UnifiedResponse<undefined>, Err, BoardRequest>(setBoard, {
        onSuccess: (res)=>{
            if (res.code === 200) {
                alert(res.msg);
                navigate("/");
            } 
        },
        onError: (err) => {
            alert("처리되지 않은 문의가 많습니다");
        }
    });

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (subject === "" || textValue === "") {
            alert("주제와 내용을 모두 입력해주세요");
        } else {
            const boardReq = { subject: subject, contents: textValue };
            setBoardMutation.mutate(boardReq);
        }
    }
    
    const SetBoardStyle : React.CSSProperties = {
        padding:"20px",
        border: "2px solid black"
    }

  return (
    <form onSubmit={onSubmitHandler} style={ CommonStyle }>
        <h1 style={{ ...TitleStyle, marginBottom:"30px" }}>문의 하기</h1>
        <div style={{ display:"flex", flexDirection: "column", justifyContent: "center", alignItems:"center" }}>
            <div style={{ ...SetBoardStyle, ...MsgAndInput, backgroundColor:"#D4F0F0", marginBottom:"0px", borderBottom:"0px"}}>
                <span>주제:</span>
                <input 
                    style={{ width:"11cm", height:"25px", marginLeft:"auto"}}
                    value={subject}
                    type="text"
                    ref={subjectRef} 
                    onChange={(e)=>{setSubject(e.target.value)}}
                />
            </div>

            <div style={SetBoardStyle}>
                <textarea 
                    style={{ width:"13.7cm", height:"14cm", marginLeft:"auto", padding:"5px", marginTop:"0.3cm"}}
                    value={textValue}
                    onChange={(e)=>{setTextValue(e.target.value)}}
                    onKeyDown={handleKeyDown}
                />
            </div>
        </div>

        <div style={{ ...ButtonDiv, marginTop:"1cm"}}>
            <button style={ButtonStyle}>제출하기</button>
        </div>
    </form>
  )
}

export default SetBoard