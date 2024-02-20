import React, { useEffect, useState } from 'react'
import { CommonStyle, InputBox, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import { MonthlyStatsReq, createMonthlyStats } from '../../api/lottoApi';

function CreateMonthlyStats() {
    const [currentYear, setCurrentYear] = useState<number>(0);
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
      const req: MonthlyStatsReq = {
        year: selectedYear,
        month: selectedMonth
      }

      CreateMonthlyStatsMutation.mutate(req);
    }

    const range = (start: number, end: number): number[] => {
      return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    };

    const years: number[] = range(currentYear - 1, currentYear);
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
        const currentYear = new Date().getFullYear();
        setCurrentYear(currentYear);
    }, [])

  return (
    <div style={ CommonStyle }>
      <h1 style={TitleStyle}>월별 통계 생성</h1>
      <form onSubmit={onSubmitHandler} style={FormStlye}>
        <select value={selectedYear} onChange={(e)=>{setSelectedYear(parseInt(e.target.value))}}>
          {years.map((year: number) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
        <span style={{ marginLeft:"5px", marginRight:"10px" }}>년</span>
        <select value={selectedMonth} onChange={(e)=>{setSelectedMonth(parseInt(e.target.value))}}>
          {months.map((month: number) => (
            <option key={month} value={String(month)}>
              {month}
            </option>
          ))}
        </select>
        <span style={{ marginLeft:"5px", marginRight:"10px" }}>월</span>
        <button style={{ marginLeft:"20px", marginRight:"20px", height:"20px"}}>생성하기</button>
      </form>
    </div>
  )
}

export default CreateMonthlyStats