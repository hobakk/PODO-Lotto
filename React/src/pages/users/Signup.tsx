import React, { useRef, useState, useEffect } from 'react'
import { SignBorder, CommonStyle, InputBox } from '../../components/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { signup } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { errorType } from '../../shared/TypeMenu';

function Signiup() {
  type InputValue = {
    email: string,
    password: string,
    nickname: string, 
  }

  const emailRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const [inputValue, setInputValue] = useState<InputValue>({
    email: "",
    password: "",
    nickname: "",
  });
  const signupMutation = useMutation(signup, {
    onSuccess: ()=>{
      console.log("회원가입 완료");
      navigate("/signin");
    }, 
    onError: (err: errorType)=>{
      alert(err.message);
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
    <div style={ SignBorder }>
      <div style={ CommonStyle }>
        <h3 style={{ fontSize: "80px"}}>Signup</h3>
        <form onSubmit={sunmitHandler} style={{ fontSize: "30px", display: "flex", flexDirection: "column", }}>
          <div>
            <span>Email : </span>
            <InputBox onChange={onChangeHandler} placeholder='test@email.com' style={{ marginLeft: "55px" }} value={inputValue.email} ref={emailRef} name='email' />
          </div>
          <div style={{ marginTop: "20px", marginBottom: "20px" }}>
            <span>Password : </span>
            <InputBox onChange={onChangeHandler} type="password" placeholder='******' value={inputValue.password} name="password" />
          </div>
          <div>
            <span>Nickname : </span>
            <InputBox onChange={onChangeHandler} type="text" placeholder='홍길동' value={inputValue.nickname} name="nickname" />
          </div>
          <div style={{ fontSize: "18px", margin: "auto", marginBottom: "1cm", marginTop: "1cm"}}>
          <div>
              <Link to="/incorrect">아이디와 비밀번호를 잊으셨나요 ?</Link>
          </div>
          <div>
              <Link to="/signip">회원 이신가요 ?</Link>
          </div>
          </div>
          <div style={{marginLeft: "auto"}}>
              <button style={{ width: "100px", height: "25px" }}>회원가입</button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default Signiup