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
    const logoutMutation = LogoutMutation();
    const [password, setPassword] = useState<string>("");
    const pwRef = useRef<HTMLInputElement>(null);
    const [isPassword, setIsPassword] = useState<boolean>(false);
    const [failCount, setFailCount] = useState<number>(0);
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
                setFailCount(0);
                setIsPassword(true);
            }
        },  
        onError: (err)=>{
            alert("비밀번호가 일치하지 않습니다");
            setFailCount(failCount + 1);
        }
    })

    useEffect(()=>{
        if (pwRef.current) pwRef.current.focus();
    }, [])

    useEffect(()=>{
        if (failCount === 5) {
            alert("비밀번호 불일치 누적으로 로그아웃 처리됩니다")
            logoutMutation.mutate();
        }
    }, [failCount])

    const submitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        checkPWMutation.mutate(password);
    }

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
                <div style={{ display:"flex", width:"15cm"}}>
                    <span>Password:</span>
                    <InputBox 
                        placeholder="********" 
                        type='passoword' 
                        value={password} 
                        onChange={passwordOnChangeHandler} 
                        ref={pwRef}
                        style={{ marginLeft:"auto", textAlign: "center", }}
                    /> 
                </div>
                <div style={{ marginLeft: "auto", width: "7.2cm", marginTop:"30px" }}>
                    <button style={{ width: "100%", height: "30px" }}>비밀번호 확인</button>
                </div>
            </form>
        ):(
            <div id="update" >
                <h1 style={{ fontSize: "80px", textAlign:"center"}}>Update</h1>
                <div style={{ width:"15cm", fontSize:"30px"}}>
                    <div style={{ display: "flex" }}>
                        <span>Email:</span>
                        <InputBox 
                            value={inputValue.email} 
                            name="email" 
                            type="text" 
                            onChange={onChangeHandler} 
                            placeholder='test@email.com' 
                            style={{ marginLeft:"auto", textAlign: "center"}}
                        />
                    </div>
                    <div style={{ display: "flex", marginTop:"30px", marginBottom:"30px"}}>
                        <span>Password:</span>
                        <InputBox 
                            value={inputValue.password} 
                            name="password" 
                            type="password" 
                            onChange={onChangeHandler} 
                            placeholder='********' 
                            autoComplete='current-password'
                            style={{ marginLeft:"auto", textAlign: "center"}}
                        />
                    </div>
                    <div style={{ display: "flex" }}>
                        <span>Nickname:</span>
                        <InputBox 
                            value={inputValue.nickname} 
                            name="nickname" 
                            type="text" 
                            onChange={onChangeHandler} 
                            placeholder='test' 
                            style={{ marginLeft:"auto", textAlign: "center"}}
                        />
                    </div>
                    <div style={{ marginLeft: "auto", width: "7.2cm", marginTop:"30px" }}>
                        <button onClick={onClickHandler} style={{ width: "100%", height: "30px" }}>수정하기</button>
                    </div>
                </div>
            </div>
        )}
    </div>
  )
}

export default InformationUpdate