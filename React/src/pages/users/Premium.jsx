import React from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { setPaid } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { useDispatch } from 'react-redux'
import { setRole } from '../../modules/userIfSlice'

function Premium() {
    const dispatch = useDispatch();

    const borderDiv = {
        border: "3px solid black",
        width: "9cm",
        height:"10cm",
        backgroundColor: "yellow",
        textAlign: "center",
        fontSize: "18px",
    }

    const setPaidMutation = useMutation(setPaid, {
        onSuccess: (res)=>{
            if (res == 200) {
                dispatch(setRole("ROLE_PAID"));
            }
        }
    });

    const onClikcHandler = () => {
        setPaidMutation.mutate();
    }

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>Premium</h1>
            <div style={borderDiv}>
                <p>매월 1일 5000원 차감</p>
                <p>통계 서비스 이용 가능</p>
                <button onClick={onClikcHandler} style={{ marginTop: "5cm" }}>프리미엄 이용하기</button>
            </div>
        </div>
    </div>
  )
}

export default Premium