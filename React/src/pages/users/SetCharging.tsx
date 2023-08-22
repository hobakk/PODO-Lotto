import React, { useEffect, useState } from 'react'
import { InputBox, SignBorder, CommonStyle } from '../../components/Styles'
import { ChargingDto, setCharges } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { AllowLogin, useAllowType } from '../../hooks/AllowType';

function SetCharging() {
    const navigate = useNavigate();
    useAllowType(AllowLogin);
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

    const chargingMutation = useMutation<UnifiedResponse<ChargingDto[]>, Err, ChargingDto>(setCharges, {
        onSuccess: (res)=>{
            if (res.code == 200) {
                navigate("/get-charging");
            }
        },
        onError: (err)=>{
            if  (err.msg) {
                alert(err.msg);
            }
        }
    })

    const submitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (inputValue.cash === 0 || inputValue.msg === "") {
            alert("값을 입력해주세요");
        } else {
            chargingMutation.mutate(inputValue);
        }
    }

  return (
    <div style={ SignBorder }>
        <form onSubmit={submitHandler} style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>Charging</h1>
            <p>금액과 입금 메세지를 적어주세요</p>
            <div style={{ display: "flex", alignItems: "center", fontSize: "22px", marginTop: "1cm" }}>
                <p style={{ marginRight: "20px"}}>Cash: </p>
                <InputBox type="number" value={inputValue.cash} placeholder='금액을 입력해주세요' onChange={onChangeHandler} name="cash"/>
            </div>
            <div style={{ display: "flex", alignItems: "center", fontSize: "22px" }}>
                <p style={{ marginRight: "20px"}}>Msg: </p>
                <InputBox type="text" value={inputValue.msg} placeholder='입금 메세지를 입력해주세요' onChange={onChangeHandler} name="msg"/>
            </div>
            <div style={{  marginTop: "1cm", marginLeft: "5cm" }}>
                <button style={{ width: "100px", height: "25px", }}>완료</button>
            </div>
        </form>
    </div>
  )
}

export default SetCharging;