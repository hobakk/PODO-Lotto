import React, { useState, useEffect } from 'react'
import { CommonStyle, InputBox, MsgAndInput, ButtonDiv, ButtonStyle } from '../../shared/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { signup, SignupRequest } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import EmailAuthentication from '../../components/EmailAuthentication';

function Signiup() {
  const navigate = useNavigate();
  const [email, setEmail] = useState<string>("");
  const [inputValue, setInputValue] = useState<{password: string, nickname: string}>({
    password: "",
    nickname: "",
  });
  const [isCorrectAuth, setIsCorrectAuth] = useState<boolean>(false);

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

  const onClickHandler = () => {
    if (isCorrectAuth) signupMutation.mutate(
      { email: email, password: inputValue.password, nickname: inputValue.nickname}
    );
    else alert("이메일 인증 후 이용하실 수 있습니다"); 
  }

  return (
    <div style={ CommonStyle }>
      <h3 style={{ fontSize: "80px"}}>회원가입</h3>
      <div style={{ fontSize: "30px", display: "flex", flexDirection: "column", width: "15cm" }}>
        <EmailAuthentication 
          isCorrectAuth={isCorrectAuth} 
          setIsCorrectAuth={setIsCorrectAuth} 
          email={email}
          setEmail={setEmail}
        />
        <div style={MsgAndInput}>
          <span>비밀번호:</span>
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
          <span>닉네임:</span>
          <InputBox 
            onChange={onChangeHandler} 
            type="text" 
            placeholder='2 ~ 10 자리를 입력해주세요' 
            value={inputValue.nickname} 
            name="nickname" 
          />
        </div>
        <div style={ButtonDiv}>
            <button 
              style={ButtonStyle}
              onClick={onClickHandler}
            >
              회원가입
            </button>
        </div>
        <div style={{ display:"flex", flexDirection:"column", fontSize: "18px", marginRight: "auto", marginTop: "1cm"}}>
          <Link to="/incorrect">아이디와 비밀번호를 잊으셨나요 ?</Link>
          <Link to="/signip" style={{ marginTop:"15px" }}>회원 이신가요 ?</Link>
        </div>
      </div>
    </div>
  )
}

export default Signiup