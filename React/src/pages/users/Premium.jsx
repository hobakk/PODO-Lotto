import React, { useEffect } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getCashNickname, setPaid } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { useDispatch, useSelector } from 'react-redux'
import { setCashNickname, setRole } from '../../modules/userIfSlice'
import { useNavigate } from 'react-router-dom'

function Premium() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const userIf = useSelector((state)=>state.userIf);

    const borderDiv = {
        border: "3px solid black",
        width: "9cm",
        height:"10cm",
        backgroundColor: "yellow",
        textAlign: "center",
        fontSize: "18px",
    }

    const getCashNicknameMutation = useMutation(getCashNickname, {
        onSuccess: (res)=>{
            console.log(res)
            dispatch(setCashNickname(res));
        }
    });
    const setPaidMutation = useMutation(setPaid, {
        onSuccess: (res)=>{
            if (res == 200) {
                dispatch(setRole("ROLE_PAID"));
                getCashNicknameMutation.mutate();
                navigate("/");
                alert("Premiun 적용 완료");
            }
        },
        onError: (err)=>{
            if (err.response) {
                alert("금액이 부족하거나 프리미엄 등급입니다");
            }
        }
    });
    const setUserMutation = useMutation(setPaid, {
        onSuccess: (res)=>{
            if (res == 200) {
                navigate("/");
                alert("Premiun 해제 완료");
            }
        },
        onError: (err)=>{
            if (err.response) {
                alert("프리미엄 등급이 아니거나 이미 해제 신청을 하셨습니다");
            }
        }    
    });

    const onClikcHandler = () => {
        setPaidMutation.mutate("");
    }
    
    useEffect(()=>{
        const setPaidElement = document.getElementById("set-paid");
        const setUserElement = document.getElementById("set-user");

        if  (userIf.role == "ROLE_PAID") {
            setPaidElement.style.display = "none";
            setUserElement.style.display = "block";
        }
    }, []);

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <div id='set-paid'>
                <h1 style={{  fontSize: "80px" }}>Premium</h1>
                <div style={borderDiv}>
                    <p>매월 1일 5000원 차감</p>
                    <p>통계 서비스 이용 가능</p>
                    <button onClick={(onClikcHandler)} style={{ marginTop: "5cm" }}>프리미엄 이용하기</button>
                </div>  
            </div>
            <div id='set-user' style={{ display: "none"}}>
                <h1 style={{  fontSize: "80px" }}>Release</h1>
                <div style={borderDiv}>
                    <p>{userIf.nickname} 님은 Premium 등급입니다</p>
                    <p>등급 변경은 매월 초에 업데이트됩니다</p>
                    <button onClick={()=>{setUserMutation.mutate("월정액 해지")}} style={{ marginTop: "5cm" }}>프리미엄 해제하기</button>
                </div>  
            </div>
        </div>
    </div>
  )
}

export default Premium