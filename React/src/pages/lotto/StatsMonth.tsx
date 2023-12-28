import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { AllMonthProps, LottoResponse, getAllMonthStats, getTopNumberForMonth } from '../../api/lottoApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';
import { UnifiedResponse } from '../../shared/TypeMenu';

function StatsMonth() {
    const [yMList, setYMList] = useState<string[]>([]);
    const [yearMonth, setYearMonth] = useState<string>("");
    const [value, setValue] = useState<LottoResponse>({countList: [], value: ""});
    const [show, setShow] = useState<boolean>(false);

    const allMonthStatsMutation = useMutation<UnifiedResponse<AllMonthProps>, any>(getAllMonthStats, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setYMList(res.data.yearMonthList);
        },
        onError: (err)=>{
            if (err.status !== 500) alert(err.message);
        }
    })

    const getMonthStatsMutation = useMutation<UnifiedResponse<LottoResponse>, any, string>(getTopNumberForMonth, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        },
        onError: (err)=>{
            if (err.status !== 500) alert(err.message);
        }
    })

    useEffect(()=>{
        allMonthStatsMutation.mutate();
    }, [])

    useEffect(()=>{
        if (yearMonth !== "") {
            getMonthStatsMutation.mutate(yearMonth);
        }
    }, [yearMonth])

  return (
    <div style={ CommonStyle }>
        <h1 style={{ fontSize: "40px", marginBottom: "1cm" }}>월별 통계</h1>
        {show ? (
            <>
                <button 
                    onClick={()=>{setShow(!show);}} 
                    style={{ width: "4cm", height: "1,5cm", marginTop: "1cm", marginBottom: "2cm"}}
                >
                    이전으로 돌아가기
                </button>
                <div style={{ marginBottom: "2cm"}}>
                    <span style={{ textAlign: "center"}}>
                        <NumSentenceResult numSentence={value.value} />
                    </span>
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
                                    onClick={()=>{
                                        setYearMonth(str);
                                        setShow(!show);
                                    }} 
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