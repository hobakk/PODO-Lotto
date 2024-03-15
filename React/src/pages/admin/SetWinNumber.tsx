import React, { useEffect, useRef, useState } from 'react'
import { ButtonDiv, ButtonStyle, CommonStyle, InputBox, MsgAndInput, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { setWinNumber } from '../../api/adminApi';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { getTimeOfWinNumber } from '../../api/winNumber';

function SetWinNumber() {
    const dateRef = useRef<HTMLInputElement>(null);
    const [inputValue, setInputValue] = useState<number>(0);

    const getTimeOfWinNunberMutation = useMutation<UnifiedResponse<number>, Err>(getTimeOfWinNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setInputValue(res.data);
        },
        onError: (err)=>{
            if (err.code) alert(err.msg);
        }
    })

    const setWinNumberMutation = useMutation<UnifiedResponse<undefined>, unknown, number>(setWinNumber, {
        onSuccess: (res)=>{
            if (res.code === 200)
            alert(res.msg);
        },
        onError: (err: any | Err)=>{
            if (err.status === 400) alert("잘못된 입력값입니다");
            else if (err.code) alert(err.msg);
        }
    })

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setWinNumberMutation.mutate(inputValue);
    }

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(parseInt(e.target.value))
    }

    useEffect(()=>{
        if (dateRef.current) {
            dateRef.current.focus()
            getTimeOfWinNunberMutation.mutate();
        }
    }, [])

  return (
    <form style={ CommonStyle } onSubmit={onSubmitHandler}>
        <h1 style={ TitleStyle }>당첨번호 등록</h1>
        <div style={MsgAndInput}>
            <span>회차:</span>
            <InputBox type='number' onChange={onChangeHandler} ref={dateRef} placeholder='1075' />
        </div>
        <div style={ButtonDiv}>
            <button style={ButtonStyle}>등록하기</button>
        </div>
    </form>
  )
}

export default SetWinNumber