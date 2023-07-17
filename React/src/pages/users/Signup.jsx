import React, { useRef, useState, useEffect } from 'react'
import { SignBorder, CommonStyle, InputBox } from '../../components/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { signup } from '../../api/noneUserApi';
import { useMutation } from 'react-query';

function Signiup() {
  const emailRef = useRef();
  const navigate = useNavigate();
  const [inputValue, setInputValue] = useState({
    email: "",
    password: "",
    nickname: "",
  });
  const signupMutation = useMutation(signup, {
    onSuccess: ()=>{
      console.log("회원가입 완료");
      navigate("/signin");
    }
  })

  const onChangeHandler = (e) => {
    setInputValue({
      ...inputValue,
      [e.target.name]: e.target.value,
    })
  }

  const sunmitHandler = (e) => {
    e.preventDefault();
    signupMutation.mutate(inputValue);
  }

  return (
    <div style={ SignBorder }>
      <div style={ CommonStyle }>
        <h3 style={{ fontSize: "80px"}}>Signup</h3>
        <form onSubmit={sunmitHandler} style={{ fontSize: "30px", display: "flex", flexDirection: "column", }}>
          <div>
            Email : <InputBox onChange={onChangeHandler} placeholder='test@email.com' style={{ marginLeft: "55px" }} value={inputValue.email} ref={emailRef} name='email'></InputBox>
          </div>
          <div style={{ marginTop: "20px", marginBottom: "20px" }}>
            Password : <InputBox onChange={onChangeHandler} type="password" placeholder='******' value={inputValue.password} name="password"></InputBox>
          </div>
          <div>
            Nickname : <InputBox onChange={onChangeHandler} type="text" placeholder='홍길동' value={inputValue.nickname} name="nickname"></InputBox>
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