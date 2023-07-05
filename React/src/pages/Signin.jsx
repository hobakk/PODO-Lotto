import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, SignBorder } from '../components/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { getCash, signin, getAccessToken } from '../api/users';
import { useDispatch } from 'react-redux';
import { setUserIf } from '../modules/userIfSlice';
import { setAccessToken } from '../modules/accessTokenSlice';

function Signin() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const signinMutation = useMutation(signin, {
      onSuccess: async ()=>{
        console.log("로그인 성공");
        getTokenMutation.mutate();
      }
    })

    const getTokenMutation = useMutation(getAccessToken, {
        onSuccess: async (accessToken)=>{
            dispatch(setAccessToken(accessToken));
            await new Promise((resolve) => setTimeout(resolve));

            getUserIfMutation.mutate();
        }
    })

    const getUserIfMutation = useMutation(getCash, {
        onSuccess: (userIf)=>{
            dispatch(setUserIf({
                cash: userIf.cash,
                nickname: userIf.nickname,
            }));
            navigate("/");
        }
    })

    const [inputValue, setInputValue] = useState({
        email: "",
        password: "",
    });
    const onChangeHandler = (e) => {
        setInputValue({
            ...inputValue,
            [e.target.name]: e.target.value,
        })
    }
    const submitHandler = (e) => {
        e.preventDefault();
        signinMutation.mutate(inputValue);
    }

    const emailRef = useRef();
    useEffect(()=>{
        emailRef.current.focus();
    }, [])

    const inputStyle = {
        width: "5cm",
        height: "25px"
    }

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h3 style={{ fontSize: "80px"}}>Login</h3>
            <form onSubmit={submitHandler} style={{ fontSize: "30px", display: "flex", flexDirection: "column", }}>
                <div style={{ marginBottom: "15px"}}>
                    Email: <input type='text' name="email" ref={emailRef} placeholder='test@email.com' onChange={onChangeHandler} style={{ marginLeft: "56px", ...inputStyle }}></input>
                </div>
                <div>
                    Password: <input type='password' name='password' placeholder='********' onChange={onChangeHandler} style={{marginBottom: "2cm", ...inputStyle}}></input>
                </div>
                <div style={{ fontSize: "18px", margin: "auto", marginBottom: "1cm"}}>
                    <div>
                        <Link to="/incorrect">비밀번호를 잊으셨나요 ?</Link>
                    </div>
                    <div>
                        <Link to="/signup">비회원 이신가요 ?</Link>
                    </div>
                </div>
                <div style={{marginLeft: "auto"}}>
                    <button style={{ width: "100px", height: "25px" }}>로그인</button>
                </div>
            </form>
        </div>
    </div>
  )
}

export default Signin