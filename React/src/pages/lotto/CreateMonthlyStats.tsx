import React, { useEffect, useState } from 'react'
import { CommonStyle, InputBox, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import { MonthlyStatsReq, createMonthlyStats } from '../../api/lottoApi';

function CreateMonthlyStats() {
    const [yearMonth, setYearMonth] = useState<{year: number, month: number}>({
      year: 0,
      month: 0,
    });

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

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setYearMonth({
          ...yearMonth,
          [name] : value,
      })
    }

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      if (yearMonth.year !== 0 && yearMonth.month !== 0) {
        const req: MonthlyStatsReq = {
          year: yearMonth.year,
          month: yearMonth.month
        }

        CreateMonthlyStatsMutation.mutate(req);
      } else {
        setYearMonth({ year: 0, month: 0 });
        alert("정확한 년월을 입력해주세요");
      }
    }

    const FormStlye : React.CSSProperties = {
      display:"flex",
      justifyContent: "center",
      alignItems:"center",
      backgroundColor:"#D4F0F0",
      width:"17cm",
      height:"2cm",
      border:"2px solid black"
    }

    useEffect(()=>{ 
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth();
        setYearMonth({ year: currentYear, month: currentMonth + 1 })
    }, [])

  return (
    <div style={ CommonStyle }>
      <h1 style={TitleStyle}>월별 통계 생성</h1>
      <form onSubmit={onSubmitHandler} style={FormStlye}>
        <InputBox value={yearMonth.year} name="year" onChange={onChangeHandler}/>
        <InputBox value={yearMonth.month} name="month" onChange={onChangeHandler} style={{ marginLeft:"auto" }}/>
        <button style={{ marginLeft:"20px", marginRight:"20px", height:"30px"}}>생성하기</button>
      </form>
    </div>
  )
}

export default CreateMonthlyStats