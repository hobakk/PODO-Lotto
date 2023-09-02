import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, CommonP, InputBox } from '../../components/Styles'
import { checkPW, update } from '../../api/userApi';
import { useMutation } from 'react-query';
import LogoutMutation from '../../components/LogoutMutation';
import { RootState } from '../../config/configStore';
import { useSelector } from 'react-redux';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { SignupRequest } from '../../api/noneUserApi';

function InformationUpdate() {
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [password, setPassword] = useState<string>("");
    const pwRef = useRef<HTMLInputElement>(null);
    const [isPassword, setIsPassword] = useState<boolean>(false);
    const [inputValue, setInputValue] = useState<SignupRequest>({
        email: "",
        password: "",
        nickname: "",
    }); 
    const [result, setResult] = useState<SignupRequest>({
        email: "",
        password: "",
        nickname: "",
    });

    const checkPWMutation = useMutation<UnifiedResponse<undefined>, Err, string>(checkPW, {
        onSuccess: (res)=>{
            if (res.code === 200) {
                setIsPassword(true);
            }
        },  
        onError: (err)=>{
            if (err.code === 500) {
                alert(err.msg);
            }
        }
    })

    useEffect(()=>{
        if (pwRef.current) {
            pwRef.current.focus();
        }
    }, [])

    const submitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        checkPWMutation.mutate(password);
    }

    const logoutMutation = LogoutMutation();

    const updateMutation = useMutation<UnifiedResponse<undefined>, Err, SignupRequest>(update, {
        onSuccess: (res)=>{
            if  (res.code === 200) {
                logoutMutation.mutate();
                alert("회원정보 수정완료 재 로그인 해주세요")
            } 
        },
        onError: (err)=>{
            if (err.exceptionType === "OverlapException") {
                alert(err.msg);
            }
        } 
    })

    const onClickHandler = () => {
        setResult((prevState)=>({
            ...prevState,
            email: inputValue.email === "" ? userIf.email : inputValue.email,
            password: inputValue.password === "" ? "" : inputValue.password,
            nickname: inputValue.nickname === "" ? userIf.nickname : inputValue.nickname,
        }));
    }

    useEffect(()=>{
        if  (result.email !== "" && result.nickname !== "") {
            updateMutation.mutate(result);  
        }
    }, [result])

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue({
            ...inputValue,
            [e.target.name]: e.target.value,
        })
    }

    const passwordOnChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => { setPassword(e.target.value) }
    
  return (
    <div style={CommonStyle}>
        {!isPassword ? (
            <form id='form' onSubmit={submitHandler} style={{ fontSize: "30px" }}>
                <h1 style={{ fontSize: "80px"}}>Identification</h1>
                Password: <InputBox placeholder="********" type='passoword' value={password} onChange={passwordOnChangeHandler} ref={pwRef}/>   
                <button style={{ marginLeft: "20px"}}>전송</button>     
            </form>
        ):(
            <div id="update" style={{ marginTop: "20px" }}>
                <h1 style={{ fontSize: "80px"}}>Update</h1>
                <div style={{ marginTop: "1px"}}>
                    <div style={{ display: "flex", alignItems: "center"}}>
                        <CommonP>Email:&nbsp;</CommonP>
                        <InputBox value={inputValue.email} name="email" type="text" onChange={onChangeHandler} placeholder='test@email.com' style={{ marginLeft: "20px"}}/>
                    </div>
                    <div style={{ display: "flex", alignItems: "center"}}>
                        <CommonP>Password:&nbsp;</CommonP>
                        <InputBox value={inputValue.password} name="password" type="Password" onChange={onChangeHandler} placeholder='********' style={{ marginLeft: "20px"}}/>
                    </div>
                    <div style={{ display: "flex", alignItems: "center"}}>
                        <CommonP>Nickname:&nbsp;</CommonP>
                        <InputBox value={inputValue.nickname} name="nickname" type="text" onChange={onChangeHandler} placeholder='test' style={{ marginLeft: "20px"}}/>
                    </div>
                    <button onClick={onClickHandler} style={{ marginTop: "2cm"}}>수정하기</button>
                </div>
            </div>
        )}
    </div>
  )
}

export default InformationUpdate