import React, { useEffect, useRef, useState } from 'react'
import { ButtonDiv, ButtonStyle, CommonStyle, InputBox, MsgAndInput } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { WinNumberRequest, setWinNumber } from '../../api/adminApi';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function SetWinNumber() {
    const dateRef = useRef<HTMLInputElement>(null);
    const [inputValue, setInputValue] = useState<WinNumberRequest>({
        date: "",
        time: 0,
        prize: 0,
        winner: 0,
        numbers: "",
    });

    const setWinNumberMutation = useMutation<UnifiedResponse<undefined>, unknown, WinNumberRequest>(setWinNumber, {
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
        const { date, time, prize, winner, numbers } = inputValue;
        if  (!date || !time || !prize || !winner || !numbers ) {
            alert("값을 전부 입력해주세요");
        } else {
            setWinNumberMutation.mutate(inputValue);
        }
    }

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setInputValue({
            ...inputValue,
            [name]:value,
        })
    }

    useEffect(()=>{
        if (dateRef.current) {
            dateRef.current.focus()
        }
    }, [])

  return (
    <form style={ CommonStyle } onSubmit={onSubmitHandler}>
        <h1 style={{ fontSize: "80px" }}>Set WinNumber</h1>
        <div style={MsgAndInput}>
            <span>Date:</span>
            <InputBox name='date' onChange={onChangeHandler} ref={dateRef} placeholder='2023-07-14' />
        </div>
        <div style={MsgAndInput}>
            <span>Time:</span>
            <InputBox name='time' onChange={onChangeHandler} placeholder='1075' />
        </div>
        <div style={MsgAndInput}>
            <span>Prize:</span>
            <InputBox name='prize'  onChange={onChangeHandler} placeholder='1000000000' />
        </div>
        <div style={MsgAndInput}>
            <span>Winner:</span>
            <InputBox name='winner' onChange={onChangeHandler} placeholder='9' />
        </div>
        <div style={MsgAndInput}>
            <span>Numbers:</span>
            <InputBox name='numbers' onChange={onChangeHandler} placeholder='10 12 14 16 42 43' />
        </div>
        <div style={ButtonDiv}>
            <button style={ButtonStyle}>등록하기</button>
        </div>
    </form>
  )
}

export default SetWinNumber