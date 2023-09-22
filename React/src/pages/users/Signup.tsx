import React, { useRef, useState, useEffect } from 'react'
import { CommonStyle, InputBox, MsgAndInput, ButtonDiv, ButtonStyle } from '../../shared/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { compareAuthCode, EmailAuthCodeRequest, sendAuthCodeToEmail, signup, SignupRequest } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';

function Signiup() {
  const emailRef = useRef<HTMLInputElement>(null);
  const [checkList, setCheckList] = useState<{sendMsg:boolean, correctCode:boolean}>({
    sendMsg: false,
    correctCode: false,
  })
  const navigate = useNavigate();
  const [inputValue, setInputValue] = useState<SignupRequest>({
    email: "",
    password: "",
    nickname: "",
  });
  const [authCode, setAuthCode] = useState<string>("");

  const sendAuthCodeToEmailMutation = useMutation<UnifiedResponse<undefined>, Err, string>(sendAuthCodeToEmail, {
    onSuccess: (res)=>{
      if (res.code === 200) {
        setCheckList({ ...checkList, sendMsg: true});
      }
    },
    onError: (err: Err)=>{
      if (err.code) alert(err.msg);
    }
  })

  const compareAuthCodeMutation = useMutation<UnifiedResponse<unknown>, unknown, EmailAuthCodeRequest>(compareAuthCode, {
    onSuccess: (res)=>{
      if (res.code === 200) {
        setCheckList({ ...checkList, correctCode: true });
      }
    },
    onError: (err: any)=>{
      if (err.status) alert(err.message);
    }
  })

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

  const authCodeOnchangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAuthCode(e.target.value);
  }

  const onClickHandler = () => {
    if (!checkList.correctCode) alert("이메일 인증 후 이용하실 수 있습니다"); 
    else signupMutation.mutate(inputValue);
  }

  useEffect(()=>{ 
    setCheckList({ sendMsg: false, correctCode: false }); 
    setAuthCode("");
  }, [inputValue.email])

  return (
    <div style={ CommonStyle }>
      <h3 style={{ fontSize: "80px"}}>Signup</h3>
      <div style={{ fontSize: "30px", display: "flex", flexDirection: "column", width: "15cm" }}>
        <div style={{ marginBottom:"30px" }}>
          <div style={{ ...MsgAndInput, width:"19cm", marginBottom:"0px"}}>
            <span>Email:</span>
            <InputBox 
              onChange={onChangeHandler} 
              placeholder='test@email.com' 
              value={inputValue.email} 
              ref={emailRef} 
              name='email'
              autoComplete='current-email'  
            />
            <button 
              style={{ width:"4cm", height:"30px"}}
              onClick={()=>sendAuthCodeToEmailMutation.mutate(inputValue.email)}
            >
              이메일 인증하기
            </button>
          </div>
          {checkList.sendMsg && (
            <div style={{ display:"flex", width:"19cm", justifyContent:"center"}}>
              <InputBox onChange={authCodeOnchangeHandler} value={authCode} />
              {!checkList.correctCode ? (
                <button 
                  style={{ width:"4cm", height:"30px"}}
                  onClick={()=>{
                    const req: EmailAuthCodeRequest = { email:inputValue.email, authCode: authCode }
                    compareAuthCodeMutation.mutate(req)
                  }}
                >
                  인증요청
                </button>
              ):(
                <span style={{ fontSize:"18px" , color: "red", marginLeft:"20px", marginRight:"56px" }}>인증 성공</span>
              )}
              
            </div>
          )}
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