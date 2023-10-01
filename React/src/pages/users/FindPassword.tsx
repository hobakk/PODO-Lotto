import React, { useState } from 'react'
import { CommonStyle, MsgAndInput, InputBox  } from '../../shared/Styles';
import { useMutation } from 'react-query';
import { findPassword } from '../../api/userApi';
import { useNavigate } from 'react-router-dom';
import { Err } from '../../shared/TypeMenu';

function FindPassword() {
    const navigate = useNavigate();
    const [inputValue, setInputValue] = useState<{[name: string]: string}>({});

    const findPasswordMutation = useMutation(findPassword, {
        onSuccess: (res)=>{
            if (res.code === 200) navigate("/signin");
        },
        onError: (err: any | Err)=>{
            if (err.code) alert(err.msg);
        }
    })

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setInputValue({
            ...inputValue,
            [name]: value,
        });
    }

    const submitHandler =  (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (inputValue["chackPassword"] === inputValue["password"]) {
            findPasswordMutation.mutate(inputValue["password"]);
        } else alert("비밀번호가 일치하지 않습니다")
    }

    return (
        <div style={CommonStyle}>
             <h1 style={{  fontSize: "60px" }}>비밀번호 찾기</h1>
            <form onSubmit={submitHandler}>
                <div style={MsgAndInput}>
                    <span>비밀번호:</span>
                    <InputBox 
                        type='password' 
                        name="password" 
                        placeholder='********'
                        onChange={onChangeHandler} 
                    />
                </div>
                <div style={MsgAndInput}>
                    <span>비밀번호 재확인:</span>
                    <InputBox 
                        type='password' 
                        name="chackPassword" 
                        placeholder='********'
                        onChange={onChangeHandler} 
                    />
                </div>
                <div style={{ display:"flex", marginLeft: "auto", width: "100%",}}>
                    <button style={{ width: "7.2cm", height: "30px", marginLeft:"auto", }}>비밀번호 설정</button>
                </div>
            </form>
           
            
        </div>
    )
}

export default FindPassword