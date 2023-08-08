import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, CommonP, CommonLink } from '../../components/Styles'
import { withdraw } from '../../api/useUserApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { setStatus } from '../../modules/userIfSlice';
import LogoutMutation from '../../components/LogoutMutation';
import { AllowLogin } from '../../components/CheckRole';

function MyPage() {
    const userIf = useSelector((state)=>state.userIf);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [role, setRole] = useState("일반");
    const [msg, setMsg] = useState("");

    useEffect(()=>{
        const formElement = document.getElementById("form");

        if (userIf.role == "ROLE_USER") {
            setRole("일반");
        } else if (userIf.role == "ROLE_PAID") {
            setRole("프리미엄");
        } else if (userIf.role == "ROLE_ADMIN") {
            formElement.style.display = "none";
            setRole("관리자");
        }
    }, [userIf])

    const logoutMutation = LogoutMutation();

    const withdrawMutation = useMutation(withdraw, {
        onSuccess: ()=>{
            dispatch(setStatus("DORMANT"));
            navigate("/");
            logoutMutation.mutate();
        }
    });

    const sunmitHandler = (e) => {
        e.preventDefault();
        if (msg === "회원탈퇴") {
            withdrawMutation.mutate(msg);
        } else {
            alert("잘못된 문자열 입력");
        }
    }

  return (
    <>
        <AllowLogin />
        <div style={CommonStyle}>
            <div id="common" style={{ marginTop: "20px" }}>
                <h1 style={{ fontSize: "80px"}}>My Page</h1>
                <div style={{ marginTop: "50px"}}>
                    <CommonP>Email:&nbsp;{userIf.email}</CommonP>
                    <CommonP>Nickname:&nbsp;{userIf.nickname}</CommonP>
                    <CommonP>Cash:&nbsp;{userIf.cash}</CommonP>
                    <CommonP>Role:&nbsp;{role}</CommonP>
                </div>
            </div>
            
            <CommonLink to="/my-page/update" style={{ color: "red", fontSize: "20px", marginTop: "30px", marginLeft: "4cm" }}>회원정보 수정하기</CommonLink>
            <div style={{ marginTop: '5cm', }}>
                <form id='form' onSubmit={sunmitHandler}>
                    <input onChange={(e)=>setMsg(e.target.value)} placeholder='회원탈퇴 입력'/>
                    <button>회원탈퇴</button>
                </form>
            </div>
        </div>
    </>
    
  )
}

export default MyPage