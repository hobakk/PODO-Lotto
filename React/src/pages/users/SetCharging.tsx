import React, { useEffect, useState } from 'react'
import { InputBox, CommonStyle, MsgAndInput, ButtonDiv, ButtonStyle } from '../../components/Styles'
import { ChargingDto, setCharges } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function SetCharging() {
    const navigate = useNavigate();
    const [inputValue, setInputValue] = useState<ChargingDto>({
        cash: 0,
        msg: "",
    });
    
    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue({
            ...inputValue,
            [e.target.name]: e.target.value,
        })
    }

    const chargingMutation = useMutation<UnifiedResponse<ChargingDto[]>, unknown, ChargingDto>(setCharges, {
        onSuccess: (res)=>{
            if (res.code == 200) {
                navigate("/get-charging");
            }
        },
        onError: (err: any | Err)=>{
            if (err.status) alert(err.message);
            else if (err.code) alert(err.msg);
        }
    })

    const submitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (inputValue.cash === 0 || inputValue.msg === "") alert("값을 입력해주세요");
        else if (inputValue.cash % 100 !== 0) alert("100원 단위로 입력해주세요");
        else chargingMutation.mutate(inputValue);
    }

  return (
    <form onSubmit={submitHandler} style={ CommonStyle }>
        <h1 style={{ marginBottom:"10px", fontSize: "80px" }}>Charging</h1>
        <p style={{ marginBottom:"70px" }}>금액과 입금 메세지를 입력해주세요</p>
        <div style={MsgAndInput}>
            <span>Cash:</span>
            <InputBox 
                type="number" 
                value={inputValue.cash} 
                placeholder='금액을 입력해주세요' 
                onChange={onChangeHandler} 
                name="cash"
            />
        </div>
        <div style={MsgAndInput}>
            <span>Msg:</span>
            <InputBox 
                type="text" 
                value={inputValue.msg} 
                placeholder='입금 메세지를 입력해주세요' 
                onChange={onChangeHandler} 
                name="msg"
            />
        </div>
        <div style={ButtonDiv}>
            <button style={ButtonStyle}>완료</button>
        </div>
    </form>
  )
}

export default SetCharging;