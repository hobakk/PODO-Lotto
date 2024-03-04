import React, { useEffect, useState } from 'react'
import { CommonStyle, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { Err, UnifiedResponse } from '../../shared/TypeMenu'
import { WinningNumberRes, checkLottoWinLastWeek } from '../../api/userApi'
import { useNavigate } from 'react-router-dom'

function CheckLottoWinLastWeek() {
    const navigate = useNavigate();
    const [value, setValue] = useState<WinningNumberRes[]>();

    const checkLottoMutation = useMutation<UnifiedResponse<WinningNumberRes[]>, any>(checkLottoWinLastWeek, {
        onSuccess: (res)=>{
            if (res.code === 200) setValue(res.data);
        },
        onError: (err: any | Err)=>{
            if (err.code === 404) {
                alert("지난주에 발급받은 번호가 없습니다");
                navigate("/");
            } else if (err.status) {
                alert(err.message);
                navigate("/");
            }
        }
    });

    useEffect(()=>{
        checkLottoMutation.mutate();
    }, [])

    return (
        <div style={CommonStyle}>
            <h1 style={ TitleStyle }>지난주 당첨 확인</h1>
            <div>
                {value?.map((item, index) => (
                  <div key={`date${index}`}>
                    <span>{item.numberList}</span>
                    <span>{item.rank}</span>
                  </div>
                ))}
            </div>
        </div>
    )
}

export default CheckLottoWinLastWeek