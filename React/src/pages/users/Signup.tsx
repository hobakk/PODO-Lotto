import React, { useRef, useState, useEffect } from 'react'
import { SignBorder, CommonStyle, InputBox, MsgAndInput, ButtonDiv, ButtonStyle } from '../../shared/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { signup, SignupRequest } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';

function Signiup() {
  const emailRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const [inputValue, setInputValue] = useState<SignupRequest>({
    email: "",
    password: "",
    nickname: "",
  });

  const signupMutation = useMutation<UnifiedResponse<undefined>, Err, SignupRequest>(signup, {
    onSuccess: (res)=>{
      if (res.code === 200 || res.code === 201) {
        alert(res.msg);
        navigate("/signin");
      }
    }, 
    onError: (err)=>{
      alert(err.msg);
    }
  })

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setInputValue({
      ...inputValue,
      [name]: value,
    })
  }

  const sunmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    signupMutation.mutate(inputValue);
  }

  return (
    <div style={ CommonStyle }>
      <h3 style={{ fontSize: "80px"}}>Signup</h3>
      <form onSubmit={sunmitHandler} style={{ fontSize: "30px", display: "flex", flexDirection: "column", width: "15cm" }}>
        <div style={MsgAndInput}>
          <span>Email:</span>
          <InputBox 
            onChange={onChangeHandler} 
            placeholder='test@email.com' 
            value={inputValue.email} 
            ref={emailRef} 
            name='email'
            autoComplete='current-email'  
          />
        </div>
        <div style={MsgAndInput}>
          <span>Password:</span>
          <InputBox 
            onChange={onChangeHandler} 
            type="password" 
            placeholder='8 자리 이상 입력해주세요' 
            value={inputValue.password} 
            name="password" 
            autoComplete='current-password' 
          />
        </div>
        <div style={MsgAndInput}>
          <span>Nickname:</span>
          <InputBox 
            onChange={onChangeHandler} 
            type="text" 
            placeholder='2 ~ 10 자리를 입력해주세요' 
            value={inputValue.nickname} 
            name="nickname" 
          />
        </div>
        <div style={ButtonDiv}>
            <button style={ButtonStyle}>회원가입</button>
        </div>
        <div style={{ display:"flex", flexDirection:"column", fontSize: "18px", marginRight: "auto", marginTop: "1cm"}}>
          <Link to="/incorrect">아이디와 비밀번호를 잊으셨나요 ?</Link>
          <Link to="/signip" style={{ marginTop:"15px" }}>회원 이신가요 ?</Link>
        </div>
      </form>
    </div>
  )
}

export default Signiup