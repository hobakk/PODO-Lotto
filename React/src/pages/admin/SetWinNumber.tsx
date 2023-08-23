import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { WinNumberRequest, setWinNumber } from '../../api/adminApi';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function SetWinNumber() {
    useAllowType(AllowOnlyAdmin);
    const dateRef = useRef<HTMLInputElement>(null);
    const [inputValue, setInputValue] = useState<WinNumberRequest>({
        date: "",
        time: 0,
        prize: 0,
        winner: 0,
        numbers: "",
    });

    const setWinNumberMutation = useMutation<UnifiedResponse<undefined>, Err, WinNumberRequest>(setWinNumber, {
        onSuccess: (res)=>{
            if (res.code === 200)
            alert(res.msg);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    })

    const InputStyle: React.CSSProperties = {
        width: "7cm",
        height: "0.8cm",
        marginBottom: "15px",
    }

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
        <h1 style={{  fontSize: "80px" }}>Set WinNumber</h1>
        <input style={InputStyle} name='date' onChange={onChangeHandler} ref={dateRef} placeholder='date : 0000-00-00' />
        <input style={InputStyle} name='time' onChange={onChangeHandler} placeholder='time : 0000' />
        <input style={InputStyle} name='prize' onChange={onChangeHandler} placeholder='prize : 0000000000' />
        <input style={InputStyle} name='winner' onChange={onChangeHandler} placeholder='winner : 0' />
        <input style={InputStyle} name='numbers' onChange={onChangeHandler} placeholder='number : 1 2 3 4 5 6' />
        <button style={InputStyle}>등록하기</button>
    </form>
  )
}

export default SetWinNumber