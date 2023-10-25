import React, { useEffect, useState } from 'react'
import { ButtonDiv, ButtonStyle, CommonStyle, TitleStyle } from '../../shared/Styles'
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
        width: "24cm",
        height:"6cm",
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
            <div style={ borderDiv }>
                <div style={{ height:"90%" }}>
                    <h1 style={ TitleStyle }>프리미엄 해제</h1>
                    <p>등급 변경은 매월 초에 업데이트됩니다</p>
                    <p>프리미엄을 해제 신청 후 납기일 기준 31일 경과전까지 유지됩니다</p>
                </div>
                <div style={ ButtonDiv }>
                    <button 
                        onClick={() => { setUserMutation.mutate("월정액 해지") }}
                        style={ ButtonStyle }
                    >
                        프리미엄 해제
                    </button>
                </div>
            </div>  
        ):(
            <div style={borderDiv}>
                <div style={{ height:"90%" }}>
                    <div style={{ display: "flex", alignItems: "center" }}>
                        <h3 style={{ fontSize: "40px" }}>프리미엄 신청</h3>
                        <p style={{ marginLeft: "auto", fontSize: "22px" }}>월 5,000원</p>
                    </div>
                    <p>사이트 개설 이후 모든 데이터 통계 및 월별 통계 이용 가능</p>
                    <p>추천 번호 발급 내역 조회 가능</p>
                </div> 
                <div style={ ButtonDiv }>
                    <button 
                        onClick={onClikcHandler}
                        style={ ButtonStyle }
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