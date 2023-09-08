import React, { useEffect, useRef, useState } from 'react'
import { ButtonDiv, ButtonStyle, CommonStyle, InputBoxStyle, MsgAndInput } from '../../components/Styles'
import { Link, useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { signin, SigninRequest } from '../../api/noneUserApi';
import { InputBox } from '../../components/Styles';
import GetUserIfMutation from '../../components/GetUserIfMutation';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';

function Signin() {
    const emailRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const getUserIfMutation = GetUserIfMutation();

    const signinMutation = useMutation<UnifiedResponse<undefined>, Err, SigninRequest>(signin,{
        onSuccess: (res)=>{
            if  (res.code === 200) {
                getUserIfMutation.mutate();
                navigate("/");
            }
        },
        onError: (err)=>{
            if  (err.exceptionType === "OverlapException") {
                alert(err.msg);
            }
        }
    });

    const [inputValue, setInputValue] = useState<SigninRequest>({
        email: "",
        password: "",
    });
    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue({
            ...inputValue,
            [e.target.name]: e.target.value,
        })
    }
    const submitHandler =  (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        signinMutation.mutate(inputValue);
    }

    useEffect(()=>{
        if (emailRef.current) {
            emailRef.current.focus();
        }
    }, [])

  return (
    <div style={ CommonStyle }>
        <h3 style={{ fontSize: "80px"}}>Login</h3>
        <form onSubmit={submitHandler} style={{ fontSize: "30px" }}>
            <div style={MsgAndInput}>
                <span>Email:</span>
                <InputBox 
                    type='text' 
                    name="email" 
                    ref={emailRef} 
                    placeholder='test@email.com'
                    autoComplete='current-email' 
                    onChange={onChangeHandler} 
                    style={InputBoxStyle}
                />
            </div>
            <div style={MsgAndInput}>
                <span>Password:</span>
                <InputBox 
                    type='password' 
                    name='password' 
                    placeholder='**************' 
                    autoComplete='current-password' 
                    onChange={onChangeHandler} 
                    style={InputBoxStyle}
                />
            </div>
            <div style={ButtonDiv}>
                <button style={ButtonStyle}>로그인</button>
            </div>
        </form>
        <div style={{ display:"flex", flexDirection:"column", fontSize: "18px", marginRight:"auto", marginTop:"30px" }}>
            <Link to="/incorrect" style={{ marginBottom:"20px" }}>비밀번호를 잊으셨나요 ?</Link>
            <Link to="/signup">비회원 이신가요 ?</Link>
        </div>
    </div>
  )
}

export default Signin