import React, { useEffect } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getCashNickname, setPaid, CashNicknameDto } from '../../api/userApi'
import { useMutation } from 'react-query'
import { useDispatch, useSelector } from 'react-redux'
import { setCashNickname, setRole } from '../../modules/userIfSlice'
import { useNavigate } from 'react-router-dom'
import { RootState } from '../../config/configStore'
import { UnifiedResponse, Err } from '../../shared/TypeMenu'
import { AllowLogin, useAllowType } from '../../hooks/AllowType'

function Premium() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const userIf = useSelector((state: RootState)=>state.userIf);
    useAllowType(AllowLogin);

    const borderDiv: React.CSSProperties = {
        border: "3px solid black",
        width: "9cm",
        height:"10cm",
        backgroundColor: "yellow",
        textAlign: "center",
        fontSize: "18px",
    }

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
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            {userIf.role !== "" && (
                userIf.role === "ROLE_PAID" ? (
                    <div>
                        <h1 style={{  fontSize: "80px" }}>Release</h1>
                        <div style={borderDiv}>
                            <p>{userIf.nickname} 님은 Premium 등급입니다</p>
                            <p>등급 변경은 매월 초에 업데이트됩니다</p>
                            <button onClick={()=>{setUserMutation.mutate("월정액 해지")}} style={{ marginTop: "5cm" }}>프리미엄 해제하기</button>
                        </div>  
                    </div>
                ):(
                    <div>
                        <h1 style={{  fontSize: "80px" }}>Premium</h1>
                        <div style={borderDiv}>
                            <p>매월 5000원 차감</p>
                            <p>통계 서비스 이용 가능</p>
                            <button onClick={(onClikcHandler)} style={{ marginTop: "5cm" }}>프리미엄 이용하기</button>
                        </div>  
                    </div>    
                )
            )}
        </div>
    </div>
  )
}

export default Premium