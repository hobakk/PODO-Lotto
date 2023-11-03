import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, MsgAndInput, InputBox, TitleStyle } from '../../shared/Styles'
import { withdraw } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { setStatus } from '../../modules/userIfSlice';
import LogoutMutation from '../../hooks/useLogoutMutation';
import { RootState } from '../../config/configStore';
import { UnifiedResponse } from '../../shared/TypeMenu';

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

    const ButtonStlye: React.CSSProperties = { 
        marginTop: "30px",
        marginLeft: "auto",
        width:"5cm",
        height: "0.8cm",
    }

    const InputBoxStyle: React.CSSProperties = { 
        marginLeft:"auto",
        textAlign: "center",
        width:"70%",
    }

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setWithdrawMsg(e.target.value);
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={ TitleStyle }>내정보</h1>
        <div style={ MsgAndInput }>
            <span style={{ width:"30%" }}>Email:</span>
            <span style={ InputBoxStyle }>{userIf.email}</span>
        </div>
        <div style={ MsgAndInput }>
            <span style={{ width:"30%" }}>Nickname:</span>
            <span style={ InputBoxStyle }>{userIf.nickname}</span>
        </div>
        <div style={ MsgAndInput }>
            <span style={{ width:"30%" }}>Cash:</span>
            <span style={ InputBoxStyle }>{userIf.cash}</span>
        </div>
        <div style={ MsgAndInput }>
            <span style={{ width:"30%" }}>Role:</span>
            <span style={ InputBoxStyle }>{role}</span>
        </div>
        <button 
            style={ ButtonStlye }
            onClick={()=>(navigate("/my-page/update"))}
        >
            내정보 수정하기
        </button>
        
        {role !== "관리자" && (
            <div style={{ marginTop: '3cm', }}>
                <form id='form' onSubmit={sunmitHandler} style={ MsgAndInput }>
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