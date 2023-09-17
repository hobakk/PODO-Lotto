import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, MsgAndInput, InputBox } from '../../components/Styles'
import { withdraw } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { setStatus } from '../../modules/userIfSlice';
import LogoutMutation from '../../components/LogoutMutation';
import { RootState } from '../../config/configStore';
import { UnifiedResponse } from '../../shared/TypeMenu';
import { Link } from 'react-router-dom';

function MyPage() {
    const userIf = useSelector((state: RootState)=>state.userIf);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const logoutMutation = LogoutMutation();
    const [role, setRole] = useState<string>("일반");
    const [withdrawMsg, setWithdrawMsg] = useState<string>("");

    useEffect(()=>{
        if (userIf.role !== "") {
            switch (userIf.role) {
                case "ROLE_USER": setRole("일반"); break;
                case "ROLE_PAID": setRole("프리미엄"); break;
                case "ROLE_ADMIN": setRole("관리자"); break;
            }
        }
    }, [userIf])

    const withdrawMutation = useMutation<UnifiedResponse<undefined>, void, string>(withdraw, {
        onSuccess: (res)=>{
            if  (res.code === 200) {
                dispatch(setStatus("DORMANT"));
                navigate("/");
                logoutMutation.mutate();
            }
        }
    });

    const sunmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (withdrawMsg === "회원탈퇴") {
            withdrawMutation.mutate(withdrawMsg);
        } else {
            alert("잘못된 문자열 입력");
        }
    }

    const LinkStyle: React.CSSProperties = { 
        textDecoration: "none",
        marginTop: "30px",
        marginLeft: "auto",
        fontSize:"20px",
        color:"red"
    }
    const InputBoxStyle: React.CSSProperties = { 
        marginLeft:"auto",
        textAlign: "center",
        width:"60%",
    }

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setWithdrawMsg(e.target.value);
    }

  return (
    <div style={CommonStyle}>
        <div>
            <h1 style={{ fontSize: "80px", textAlign:"center"}}>My Page</h1>
            <div style={{ marginTop: "50px" }}>
                <div style={ MsgAndInput }>
                    <span style={{ width:"40%" }}>Email:</span>
                    <span style={ InputBoxStyle }>{userIf.email}</span>
                </div>
                <div style={ MsgAndInput }>
                    <span style={{ width:"40%" }}>Nickname:</span>
                    <span style={ InputBoxStyle }>{userIf.nickname}</span>
                </div>
                <div style={ MsgAndInput }>
                    <span style={{ width:"40%" }}>Cash:</span>
                    <span style={ InputBoxStyle }>{userIf.cash}</span>
                </div>
                <div style={ MsgAndInput }>
                    <span style={{ width:"40%" }}>Role:</span>
                    <span style={ InputBoxStyle }>{role}</span>
                </div>
            </div>
        </div>
        <Link to="/my-page/update" style={LinkStyle}>회원정보 수정하기</Link>
        
        {role !== "관리자" && (
            <div style={{ marginTop: '5cm', }}>
                <form id='form' onSubmit={sunmitHandler} style={{ ...MsgAndInput, width:"12cm"}}>
                    <InputBox 
                        onChange={onChangeHandler} 
                        type='text' 
                        placeholder='회원탈퇴 입력'
                    />
                    <button style={{ width:"3cm", height:"31px" }}>회원탈퇴</button>
                </form>
            </div>
        )}
    </div>
  )
}

export default MyPage