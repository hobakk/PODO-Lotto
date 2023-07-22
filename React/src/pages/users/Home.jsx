import React, { useEffect, useState } from 'react'
import { getWinNumber } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { CommonStyle, WinNumberStyle } from '../../components/Styles';
import { CommonLink } from '../../components/Styles';

function Home() {
  const [value, setValue] = useState([]);
  const [isEmpty, setBoolean] = useState(true);
  const getWinnumberMutation = useMutation(getWinNumber, {
    onSuccess: (res)=>{
      const sortedValue = res.slice().sort((a, b) => {
        return new Date(b.date) - new Date(a.date);
      });
      setValue(sortedValue);
      setBoolean(false);
    },
    onError: (err)=>{
      if (err.status === 500) {
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
  
  const changingColor = (num, index) => {
    let color = "";
    if (num <= 10) {
      color = "#eab541";
    } else if (num <= 20) {
      color = "#4331b0";
    } else if (num <= 30) {
      color = "#e61940";
    } else if (num <= 40) {
      color = "#545f69";
    } else {
      color = "#17c23a";
    }
    return <WinNumberStyle key={`numbers-${index}`} color={color} style={{ marginRight: "7px"}}>{num}</WinNumberStyle>
  }

  const Rectangle = {
    border: "3px solid black", 
    backgroundColor: "#D4F0F0",
    padding: "15px",
    marginBottom: "5px",
    width: "20cm"
  }
  const ButtonStyle = {
    width: "3cm",
    height: "30px",
    marginLeft: "10px"
  }
  const SpanStyle = {
    fontWeight: "bold",
    color: "#011815",
  }

  return (
    <div style={CommonStyle}>
      <div style={{ marginTop: "1.0cm", marginLeft: "auto", marginBottom: "5px"}}>
        <CommonLink to={"/"} style={{ fontWeight: "bold", }}>추천 번호 구매하기</CommonLink>
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
                    {result.numList.map((num, index)=>changingColor(num, index))}
                  </div>
                  <span style={{...SpanStyle, marginLeft: "5px", marginRight: "5px",}}>+</span>
                  <div style={{ display: "flex", marginLeft: "5px"}}>{changingColor(result.bonus)}</div>
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