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
    const [selectedYear, setSelectedYear] = useState<string>('');
    const [yearRange, setYearRange] = useState<{minYear: number, maxYear:number}>({
        minYear: 0,
        maxYear: 0
    });

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
        if (yMList.length !== 0) {
            const min = yMList[0].split("-")[0];
            const max = yMList[yMList.length -1].split("-")[0];
            setYearRange({
                minYear: parseInt(min) +1,
                maxYear: parseInt(max) +1
            });
        }
    }, [yMList])

    useEffect(()=>{
        if (yearMonth !== "") {
            getMonthStatsMutation.mutate(yearMonth);
        }
    }, [yearMonth])

    const range = (start: number, end: number): number[] => {
        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    };

    const years: number[] = range(yearRange.minYear - 1, yearRange.maxYear);

    const DivStlye : React.CSSProperties = {
        display:"flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems:"center",
    }

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
            <div style={DivStlye}>
                <div style={{ ...DivStlye, width:"6cm", height:"2cm", border:"2px solid black",
                    backgroundColor:"#D4F0F0", marginBottom:"30px" }}
                >
                    <select 
                        value={selectedYear} 
                        onChange={(e)=>{setSelectedYear(e.target.value)}}
                        style={{ width:"3cm", height:"0.7cm", textAlign:"center", fontSize:"16px" }}
                    >
                        <option value="">년도 선택</option>
                        {years.map((year: number) => (
                            <option key={year} value={year}>
                                {year}
                            </option>
                        ))}
                    </select>
                </div>

                {yMList.length !== 0 ? (
                    yMList.map((str, index)=>{
                        if (str.split("-")[0] === selectedYear) {
                            return (
                                <div key={`buttons${index}`}>
                                    <button 
                                        onClick={()=>{
                                            setYearMonth(str);
                                            setShow(!show);
                                        }} 
                                        style={{ width: "3cm", height: "1cm", fontSize: "20px", marginBottom:"5px" }} 
                                    >
                                        {str}
                                    </button>
                                </div>
                            )  
                        }
                    })
                ):(<div>월 통계가 존재하지 않습니다</div>)}
            </div>
        )}
    </div>
  )
}

export default StatsMonth