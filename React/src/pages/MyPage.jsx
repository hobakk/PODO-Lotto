import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, CommonP } from '../components/Styles'
import { withdraw } from '../api/useUserApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { setStatus } from '../modules/userIfSlice';

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
        } else {
            alert("비정상 접근")
        }
    }, [userIf])

    const withdrawMutation = useMutation(withdraw, {
        onSuccess: ()=>{
            dispatch(setStatus("DORMANT"));
            navigate("/");
        }
    });

    const sunmitHandler = (e) => {
        e.preventDefault();
        withdrawMutation.mutate(msg);
    }

  return (
    <div style={CommonStyle}>
        <h1>My Page</h1>
        <div style={{ marginTop: "20px", }}>
            <CommonP>Email:&nbsp;{userIf.email}</CommonP>
            <CommonP>Nickname:&nbsp;{userIf.nickname}</CommonP>
            <CommonP>Cash:&nbsp;{userIf.cash}</CommonP>
            <CommonP>Role:&nbsp;{role}</CommonP>
        </div>
        <form id='form' onSubmit={sunmitHandler}>
            <input onChange={(e)=>setMsg(e.target.value)} placeholder='회원탈퇴'/>
            <button>회원탈퇴</button>
        </form>
        
    </div>
  )
}

export default MyPage