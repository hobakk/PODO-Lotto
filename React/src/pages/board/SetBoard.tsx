import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, InputBox, TitleStyle, MsgAndInput, ButtonDiv, ButtonStyle } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { Err, UnifiedResponse } from '../../shared/TypeMenu'
import { BoardRequest, setBoard } from '../../api/boardApi'

function SetBoard() {
    const subjectRef = useRef<HTMLInputElement>(null);
    const [inputValue, setInputValue] = useState<BoardRequest>({
        subject: "",
        contents: ""
    });

    const setBoardMutation = useMutation<UnifiedResponse<undefined>, Err, BoardRequest>(setBoard, {
        onSuccess: (res)=>{
            if (res.code === 200) {
                alert(res.msg);
            } 
        },
        onError: (err) => {
            alert("처리되지 않은 문의가 많습니다");
        }
    });

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value }: any = e.target;

        setInputValue({
            ...inputValue,
            [name]: value,
        })
    }

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const { subject, contents } = inputValue;

        if (subject === "" || contents === "") {
            alert("주제와 내용을 모두 입력해주세요");
        } else setBoardMutation.mutate(inputValue);
    }

  return (
    <form onSubmit={onSubmitHandler} style={ CommonStyle }>
        <h1 style={ TitleStyle }>문의 하기</h1>
        <div style={MsgAndInput}>
            <span>주제:</span>
            <InputBox 
                style={{ width:"11cm"}}
                name="subject"
                value={inputValue.subject}
                type="text"
                ref={subjectRef} 
                onChange={onChangeHandler}
            />
        </div>
        <InputBox 
            style={{ width:"14cm", height:"14cm", marginTop:"1cm"}}
            name="contents"
            value={inputValue.contents}
            type="text"
            onChange={onChangeHandler}
        />

        <div style={{ ...ButtonDiv, marginTop:"1cm"}}>
            <button style={ButtonStyle}>조회하기</button>
        </div>
    </form>
  )
}

export default SetBoard