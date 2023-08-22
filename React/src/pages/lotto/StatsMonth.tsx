import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { AllMonthProps, LottoResponse, getAllMonthStats, getTopNumberForMonth } from '../../api/lottoApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { AllowNotRoleUser, useAllowType } from '../../hooks/AllowType';

function StatsMonth() {
    useAllowType(AllowNotRoleUser);
    const [yMList, setYMList] = useState<string[]>([]);
    const [yearMonth, setYearMonth] = useState<string>("");
    const [value, setValue] = useState<LottoResponse>({countList: [], value: ""});
    const [render, setRender] = useState<boolean>(true);

    const allMonthStatsMutation = useMutation<UnifiedResponse<AllMonthProps>, Err>(getAllMonthStats, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setYMList(res.data.yearMonthList);
        },
        onError: (err)=>{
            if (err.code === 500) {
                alert(err.msg);
            }
        }
    })

    const getMonthStatsMutation = useMutation<UnifiedResponse<LottoResponse>, Err, string>(getTopNumberForMonth, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        },
        onError: (err)=>{
            if (err.code === 500) {
                alert(err.msg);
            }
        }
    })

    useEffect(()=>{
        allMonthStatsMutation.mutate();
    }, [render])

    useEffect(()=>{
        if (yearMonth !== "") {
            getMonthStatsMutation.mutate(yearMonth);
        }
    }, [yearMonth])

    useEffect(()=>{
        setRender(!render);
    }, [value])

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height: "1cm"}}>Stats Month</h1>
        {value.value !== "" ? (
            <>
                <button 
                    onClick={()=>{setValue({countList: [], value: ""});}} 
                    style={{ width: "4cm", height: "1,5cm", marginTop: "1cm", marginBottom: "2cm"}}
                >
                    이전으로 돌아가기
                </button>
                <div style={{ marginBottom: "2cm"}}>
                    <span style={{ textAlign: "center"}}>{NumSentenceResult(value.value)}</span>
                </div>
                {value.countList.length !== 0 &&(
                    <StatsContainer res={value.countList}/>
                )}
            </>
        ):(
            <div style={{ marginTop: "2cm"}}>
                {yMList.length !== 0 ? (
                    yMList.map((str, index)=>{
                        return (
                            <div key={`buttons${index}`}>
                                <button 
                                    onClick={()=>setYearMonth(str)} 
                                    style={{ width: "3cm", height: "1cm", fontSize: "20px" }} 
                                >
                                    {str}
                                </button>
                            </div>
                        )
                    })
                ):(<div>월 통계가 존재하지 않습니다</div>)}
            </div>
        )}
    </div>
  )
}

export default StatsMonth