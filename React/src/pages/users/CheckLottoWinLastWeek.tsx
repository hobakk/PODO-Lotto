import React, { useEffect, useState } from 'react'
import { BottomDividingLine, CommonStyle, TitleStyle, TopDividingLine } from '../../shared/Styles'
import { useMutation } from 'react-query'
import { Err, UnifiedResponse } from '../../shared/TypeMenu'
import { WinningNumberRes, checkLottoWinLastWeek } from '../../api/userApi'
import { useNavigate } from 'react-router-dom'
import { ChangingNumStyle, NumSentenceResult } from '../../components/Manufacturing'

function CheckLottoWinLastWeek() {
    const navigate = useNavigate();
    const [value, setValue] = useState<WinningNumberRes[]>([]);

    const checkLottoMutation = useMutation<UnifiedResponse<WinningNumberRes[]>>(checkLottoWinLastWeek, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) setValue(res.data);
        },
        onError: (err: any | Err)=>{
            if (err.code) {
                alert("지난주에 발급받은 번호가 없습니다");
                navigate("/");
            }
            if (err.status) {
                alert(err.message);
                navigate("/");
            }
        }
    });

    const overlapStyle: React.CSSProperties = {
        display:"flex",
        justifyContent: "center",
        alignItems:"center",
    }

    useEffect(()=>{
        checkLottoMutation.mutate();
    }, []);

    return (
        <div style={CommonStyle}>
            <h1 style={ TitleStyle }>지난주 당첨 확인</h1>
            <div style={{ backgroundColor:"#D4F0F0" }}>
                <TopDividingLine width={"20cm"}/>
                {value?.length === 0? (
                    <div style={{ ...overlapStyle, fontSize:"18px",  }}>
                        아쉽게도
                        <span style={{ color:"red", marginLeft:"5px", marginRight:"5px"}}>당첨</span>
                        되지 않으셨습니다
                    </div>
                ):(
                    <div>
                        {value?.map((item, index) => (
                        <div 
                            key={`date${index}`} 
                            style={{ display:"flex", width:"20cm" }}
                        >
                            <div style={{ ...overlapStyle, width:"20cm" }}>
                                <NumSentenceResult numSentence={item.numberSentence} />
                                <span style={{ fontSize:"24px", color:"red", marginLeft:"2cm" }}>{item.rank}</span>
                                <span style={{ fontSize:"24px" }}>등 당첨</span>
                            </div>
                        </div>
                        ))}
                    </div>
                )}
                <BottomDividingLine width={"20cm"} style={{ marginTop:"20px"}}/>
            </div>
            
        </div>
    )
}

export default CheckLottoWinLastWeek