import React, { useRef, useState, useEffect } from 'react'
import { InputBox, MsgAndInput, ButtonDiv, ButtonStyle } from '../shared/Styles'
import { compareAuthCode, EmailAuthCodeRequest, sendAuthCodeToEmail, signup, SignupRequest } from '../api/noneUserApi';
import { useMutation } from 'react-query';
import { UnifiedResponse, Err } from '../shared/TypeMenu';

function EmailAuthentication({ isCorrectAuth, setIsCorrectAuth, email, setEmail }: {
  isCorrectAuth: boolean;
  setIsCorrectAuth: React.Dispatch<React.SetStateAction<boolean>>;
  email: string;
  setEmail: React.Dispatch<React.SetStateAction<string>>;
}) {
    const emailRef = useRef<HTMLInputElement>(null);
    const [authCode, setAuthCode] = useState<string>("");
    const [sendEmail, setSendEmail] = useState<boolean>(false);
    const [seconds, setSeconds] = useState<number>(300);

    useEffect(()=>{ 
      setIsCorrectAuth(false); 
      setAuthCode("");
      setSendEmail(false);
      setSeconds(300);
    }, [email])

    useEffect(()=>{
      if (seconds === 0) {
        setSendEmail(false);
        setSeconds(300);
      }
    }, [seconds])

    const startCountdown = () => {
      let timerId: NodeJS.Timeout | undefined;
  
      const updateCountdown = () => {
        if (seconds > 0) {
          setSeconds((prevSeconds) => prevSeconds - 1);
        } else {
          clearInterval(timerId);
          setSendEmail(false);
          setSeconds(300);
        }
      };
  
      timerId = setInterval(updateCountdown, 1000);
  
      return () => {
        if (timerId !== null) {
          clearInterval(timerId);
        }
      };
    };

    const sendAuthCodeToEmailMutation = useMutation<UnifiedResponse<undefined>, Err, string>(sendAuthCodeToEmail, {
        onSuccess: (res)=>{
          if (res.code === 200) {
            startCountdown();
          }
        },
        onError: (err: Err)=>{
          if (err.code) alert(err.msg);
        }
    })

    const compareAuthCodeMutation = useMutation<UnifiedResponse<unknown>, unknown, EmailAuthCodeRequest>(compareAuthCode, {
      onSuccess: (res)=>{
        if (res.code === 200) {
          setIsCorrectAuth(true); 
        }
      },
      onError: (err: any)=>{
        if (err.status) alert(err.message);
      }
    })

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
      setEmail(e.target.value);
    }
    const authCodeOnchangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
      setAuthCode(e.target.value);
    }

    return (
      <>
        <div style={{ marginBottom:"30px" }}>
          <div style={{ ...MsgAndInput, width:"18cm", marginBottom:"0px"}}>
            <span>이메일:</span>
            <InputBox 
              onChange={onChangeHandler} 
              placeholder='test@email.com' 
              value={email} 
              ref={emailRef} 
              name='email'
              autoComplete='current-email'  
            />
            {sendEmail ? (
              isCorrectAuth ? (
                <div style={{ width:"4cm", height:"30px"}}></div>
              ):(
                <div style={{ width:"4cm", height:"30px"}}>
                  <span style={{ marginLeft:"30px", color:"red"}}>{seconds}</span>
                </div>
              )
            ):(
              <button 
                style={{ width:"4cm", height:"30px"}}
                onClick={()=>{
                  setSendEmail(true);
                  sendAuthCodeToEmailMutation.mutate(email)
                }}
              >
                이메일 인증하기
              </button>
            )}
          </div>
        </div>
        <div style={{ ...MsgAndInput, width:"18cm", }}>
          <span>인증번호 확인:</span>
          <InputBox onChange={authCodeOnchangeHandler} value={authCode} />
          {!isCorrectAuth ? (
              <button 
                style={{ width:"4cm", height:"30px"}}
                onClick={()=>{
                  const req: EmailAuthCodeRequest = { email:email, authCode: authCode }
                  compareAuthCodeMutation.mutate(req)
                }}
              >
                인증요청
              </button>
            ):(
              <span style={{ fontSize:"18px" , color: "red", marginLeft:"20px", marginRight:"56px" }}>
                인증 성공
              </span>
            )}
        </div>
      </>
    )
}

export default EmailAuthentication;