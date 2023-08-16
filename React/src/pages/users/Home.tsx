import React, { useEffect, useState } from 'react'
import { getWinNumber } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { CommonStyle, CommonLink, } from '../../components/Styles';
import { ChangingNumStyle } from '../../components/Manufacturing';
import { Res, WinNumber, errorType } from '../../shared/TypeMenu';

function Home() {
  const [value, setValue] = useState<WinNumber[]>([]);
  const [isEmpty, setBoolean] = useState<boolean>(true);
  
  const getWinnumberMutation = useMutation(getWinNumber, {
    onSuccess: (res: Res)=>{
      if (res.code === 200) {
        const sortedValue = res.data.winNumberList.slice().sort((a: WinNumber, b: WinNumber) => {
          return new Date(b.date).getTime() - new Date(a.date).getTime();
        });
        setValue(sortedValue);
        setBoolean(false);
      }
    },
    onError: (err: errorType)=>{
      if (err.code === 500) {
        setValue([]);
      }
    }
  })

  useEffect(()=>{
    if (isEmpty === true) {
      getWinnumberMutation.mutate();
    }
  }, [])
  useEffect(()=>{
    if (isEmpty === true) {
      getWinnumberMutation.mutate();
    }
  }, [isEmpty])

  const Rectangle: React.CSSProperties = {
    border: "3px solid black", 
    backgroundColor: "#D4F0F0",
    padding: "15px",
    marginBottom: "5px",
    width: "20cm"
  }
  const ButtonStyle: React.CSSProperties = {
    width: "3cm",
    height: "30px",
    marginLeft: "10px"
  }
  const SpanStyle: React.CSSProperties = {
    fontWeight: "bold",
    color: "#011815",
  }

  return (
    <div style={CommonStyle}>
      <div style={{ marginTop: "1.0cm", marginLeft: "auto", marginBottom: "5px"}}>
        <CommonLink to={"/buynum"} color='blue' style={{ fontWeight: "bold", }}>추천 번호 구매하기</CommonLink>
        <button style={ButtonStyle} onClick={()=>{setBoolean(true)}}>새로고침</button>
      </div>
      <div style={{ fontSize: "20px" }}>
        {isEmpty ? (
          <div>
            당첨번호 데이터가 존재하지 않습니다
          </div>
        ):(
          value.map((result)=>{
            return (
              <div key={result.time} style={Rectangle}>
                <div style={{ display: "flex", height: "1.2cm" }}>
                  <p>
                    <span style={SpanStyle}>{result.time}</span>회 당첨결과
                  </p>
                  <p style={{ marginLeft: "auto"}}>
                    <span style={SpanStyle}>{result.date}</span>추첨
                  </p>
                </div>
                <div style={{ display: "flex", height: "1.2cm" }}>
                  <p>
                    1등 총 당첨금 <span style={SpanStyle}>{result.prize.toLocaleString()}</span>원
                  </p>
                  <p style={{ marginLeft: "auto"}}>
                    당첨인원 <span style={SpanStyle}>{result.winner}</span>명
                  </p>
                </div>
                <div style={{ display: "flex", height: "1.5cm", marginTop: "10px", justifyContent: "center", textAlign: "center", alignItems: "center"}}>
                  <div style={{ display: "flex", marginLeft: "auto"}}>
                    {result.topNumberList.map((num, index)=>{
                      return <div key={index}>{ChangingNumStyle({num, index})}</div>
                    })}
                  </div>
                  <span style={{...SpanStyle, marginLeft: "5px", marginRight: "5px",}}>+</span>
                  <div style={{ display: "flex", marginLeft: "5px"}}>{ChangingNumStyle({num: result.bonus, index: 0})}</div>
                </div>
              </div>
            )})
          )
        }
      </div>
    </div>
  )
}

export default Home