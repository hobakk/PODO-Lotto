import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../shared/Styles'
import { getCashNickname, setPaid, CashNicknameDto } from '../../api/userApi'
import { useMutation } from 'react-query'
import { useDispatch, useSelector } from 'react-redux'
import { setCashNickname, setRole } from '../../modules/userIfSlice'
import { useNavigate } from 'react-router-dom'
import { RootState } from '../../config/configStore'
import { UnifiedResponse, Err } from '../../shared/TypeMenu'

function Premium() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [userRole, setUserRole] = useState<string>("ROLE_USER");

    const borderDiv: React.CSSProperties = {
        border: "3px solid black",
        width: "14cm",
        height:"18cm",
        padding:"50px",
        backgroundColor: "yellow",
        fontSize: "18px",
    }

    useEffect(()=>{ setUserRole(userIf.role) }, [userIf])
    useEffect(()=>{ 
        if (userIf.role === "ROLE_ADMIN") {
            alert("관리자는 이용할 수 없습니다");
            navigate("/");
        } 
    })

    const getCashNicknameMutation = useMutation<UnifiedResponse<CashNicknameDto>>(getCashNickname, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                dispatch(setCashNickname(res.data));
            }
        }
    });

    const setPaidMutation = useMutation<UnifiedResponse<undefined>, Err, string>(setPaid, {
        onSuccess: (res)=>{
            if (res.code == 200) {
                dispatch(setRole("ROLE_PAID"));
                getCashNicknameMutation.mutate();
                navigate("/");
                alert("Premiun 적용 완료");
            }
        },
        onError: (error)=>{    
            if (error.code === 500) {
                alert(error.msg);
            }
        }
    });

    const setUserMutation = useMutation<UnifiedResponse<undefined>, Err, string>(setPaid, {
        onSuccess: (res)=>{
            if (res.code == 200) {
                navigate("/");
                alert("Premiun 해제 완료");
            }
        },
        onError: (error)=>{
            if (error.code === 400) {
                alert(error.msg);
            }
        }    
    });

    const onClikcHandler = () => {
        setPaidMutation.mutate("");
    }

  return (
    <div style={ CommonStyle }>
        {userRole === "ROLE_PAID" ? (
            <div style={borderDiv}>
                <div style={{ height:"80%" }}>
                    <h1 style={{ fontSize: "80px", textAlign:"center" }}>Release</h1>
                    <p>{userIf.nickname} 님은 Premium 등급입니다</p>
                    <p>등급 변경은 매월 초에 업데이트됩니다</p>
                    <p>프리미엄을 해제 신청 후 납기일 기준 31일 경과전까지 유지됩니다</p>
                </div>
                <div style={{ textAlign:"center" }}>
                    <button 
                        onClick={() => { setUserMutation.mutate("월정액 해지") }}
                        style={{ width: "7cm", height:"1cm"}}
                    >
                        프리미엄 해제하기
                    </button>
                </div>
            </div>  
        ):(
            <div style={borderDiv}>
                <div style={{ height:"80%" }}>
                    <h1 style={{ fontSize: "80px", textAlign:"center" }}>Premium</h1>
                    <p>매월 5000원 차감</p>
                    <p>통계 서비스 이용 가능</p>
                </div> 
                <div style={{ textAlign:"center" }}>
                    <button 
                        onClick={onClikcHandler}
                        style={{ width: "7cm", height:"1cm"}}
                    >
                        프리미엄 이용하기
                    </button>
                </div>
            </div>    
        )}
    </div>
  )
}

export default Premium