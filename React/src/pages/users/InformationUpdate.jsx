import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, CommonP, InputBox } from '../../components/Styles'
import { checkPW, update, logout } from '../../api/useUserApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import LogoutMutation from '../../components/LogoutMutation';

function InformationUpdate() {
    const userIf = useSelector((state)=>state.userIf);
    const [password, setPassword] = useState("");
    
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const pwRef = useRef();

    const checkPWMutation = useMutation(checkPW, {
        onSuccess: (res)=>{
            const formElement = document.getElementById("form");
            const updateElement = document.getElementById("update");
            if  (res == 200) {
                formElement.style.display = "none";
                updateElement.style.display = "block";
            } 
        },  
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
            }
        }
    })
    const [inputValue, setInputValue] = useState({
        email: "",
        password: "",
        nickname: "",
    }); 
    const [result, setResult] = useState({
        email: "",
        password: "",
        nickname: "",
    });

    useEffect(()=>{
        pwRef.current.focus();
    }, [])

    const submitHandler = (e) => {
        e.preventDefault();
        checkPWMutation.mutate(password);
    }

    const logoutMutation = LogoutMutation();

    const updateMutation = useMutation(update, {
        onSuccess: (res)=>{
            if  (res == 200) {
                logoutMutation.mutate();
                alert("회원정보 수정완료 재 로그인 해주세요")
            } 
        },
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
            } else {
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
        console.log(result)
        if  (result.email !== "" && result.nickname !== "") {
            updateMutation.mutate(result);  
        }
    }, [result])

    const onChangeHandler = (e) => {
        setInputValue({
            ...inputValue,
            [e.target.name]: e.target.value,
        })
    }
    
  return (
    <div style={CommonStyle}>
        <form id='form' onSubmit={submitHandler} style={{ fontSize: "30px" }}>
            <h1 style={{ fontSize: "80px"}}>Identification</h1>
            Password: <InputBox placeholder="********" type='passoword' value={password} onChange={(e)=>{setPassword(e.target.value)}} ref={pwRef}/>   
            <button style={{ marginLeft: "20px"}}>전송</button>     
        </form>

        <div id="update" style={{ marginTop: "20px", display: "none" }}>
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
    </div>
  )
}

export default InformationUpdate