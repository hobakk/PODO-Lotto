import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, SignBorder } from '../components/Styles'
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { signin } from '../api/noneUserApi';
import { useDispatch } from 'react-redux';
import { setUserIf } from '../modules/userIfSlice';
import { getInformation } from '../api/useUserApi';
import { InputBox } from '../components/Styles';

function Signin() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const signinMutation = useMutation(signin,{
        onSuccess: ()=>{
            console.log("로그인 완료")
            getUserIfMutation.mutate();
        }
    });

    const getUserIfMutation = useMutation(getInformation, {
        onSuccess: (userIf)=>{
            dispatch(setUserIf(userIf));
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
    const submitHandler =  (e) => {
        e.preventDefault();
        signinMutation.mutate(inputValue);
    }

    const emailRef = useRef();
    useEffect(()=>{
        emailRef.current.focus();
    }, [])

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h3 style={{ fontSize: "80px"}}>Login</h3>
            <form onSubmit={submitHandler} style={{ fontSize: "30px", display: "flex", flexDirection: "column", }}>
                <div style={{ marginBottom: "15px"}}>
                    Email: <InputBox type='text' name="email" ref={emailRef} placeholder='test@email.com' onChange={onChangeHandler} style={{ marginLeft: "56px"}}></InputBox>
                </div>
                <div>
                    Password: <InputBox type='password' name='password' placeholder='********' onChange={onChangeHandler} style={{marginBottom: "2cm"}}></InputBox>
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