import React, { useEffect, useState } from 'react'
import { CommonStyle, InputBox, SelectStyle, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import { MonthlyStatsReq, createMonthlyStats } from '../../api/lottoApi';

function CreateMonthlyStats() {
    const [current, setCurrent] = useState<{year: number, month:number}>({
      year: 0,
      month: 0
    });
    const [selectedYear, setSelectedYear] = useState<number>(0);
    const [selectedMonth, setSelectedMonth] = useState<number>(0);

    const CreateMonthlyStatsMutation = useMutation<UnifiedResponse<undefined>, any, MonthlyStatsReq>(createMonthlyStats, {
      onSuccess: (res)=>{
          if (res.code === 200)
          alert(res.msg);
      },
      onError: (err: any | Err)=>{
          if (err.status) alert(err.message);
          else alert(err.msg)
      }
    })

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      if ( current.year === selectedYear && current.month <= selectedMonth) {
        alert("당월 이전의 날짜만 서비스를 이용할 수 있습니다");
      } else {
        const req: MonthlyStatsReq = {
          year: selectedYear,
          month: selectedMonth
        }

        CreateMonthlyStatsMutation.mutate(req);
      }
    }

    const range = (start: number, end: number): number[] => {
      return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    };

    const years: number[] = range(current.year - 1, current.year);
    const months: number[] = range(1, 12);

    const FormStlye : React.CSSProperties = {
      display:"flex",
      justifyContent: "center",
      alignItems:"center",
      backgroundColor:"#D4F0F0",
      width:"12cm",
      height:"2cm",
      border:"2px solid black"
    }

    useEffect(()=>{ 
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth();
        setCurrent({ year: currentYear, month: currentMonth + 1 });
    }, [])

  return (
    <div style={ CommonStyle }>
      <h1 style={TitleStyle}>월별 통계 생성</h1>
      <form onSubmit={onSubmitHandler} style={FormStlye}>
        <select 
          value={selectedYear} 
          onChange={(e)=>{setSelectedYear(parseInt(e.target.value))}}
          style={SelectStyle}
        >
          <option value="">년도 선택</option>
          {years.map((year: number) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
        <select 
          value={selectedMonth} 
          onChange={(e)=>{setSelectedMonth(parseInt(e.target.value))}}
          style={{ ...SelectStyle, marginLeft:"20px"}}
        >
          <option value="">월 선택</option>
          {months.map((month: number) => (
            <option key={month} value={String(month)}>
              {month}
            </option>
          ))}
        </select>
        <button style={{ marginLeft:"20px", marginRight:"20px", ...SelectStyle}}>생성하기</button>
      </form>
    </div>
  )
}

export default CreateMonthlyStats