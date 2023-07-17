import React, { useEffect, useState } from 'react'
import { getWinNumber } from '../../api/noneUserApi';
import { useMutation } from 'react-query';
import { CommonStyle } from '../../components/Styles';
import { CommonLink } from '../../components/Styles';

function Home() {
  const [value, setValue] = useState("");
  const getWinnumberMutation = useMutation(getWinNumber, {
    onSuccess: (res)=>{
      setValue(res);
    }
  })
  useEffect(()=>{
    if (value === "") {
      getWinnumberMutation.mutate();
    }
  }, [])

  useEffect(()=>{
    if (value !== null || value !== "") {
      console.log(value);
    }
  }, [value])


  const Rectangle = {
    width: "20cm", 
    height: "8cm", 
    border: "3px solid black", 
    marginTop: "4cm",
  }

  return (
    <div style={CommonStyle}>
      <div style={Rectangle}>
        {/* {value.map(result=>{
          return (
            <div key={result.time} style={{ display: "flex", }}>
              <p>({result.date} 추첨)</p>
              <p>{result.time}회 당첨결과</p>
              <p>1등 당첨금 {result.prize}</p>
              <p>{result.winner}명</p>
              <p>{result.numList}</p>
              <p>+{result.bonus}</p>
            </div>
          )
        })} */}
      </div>
      <CommonLink to={"/"}>추천 번호 구매하기</CommonLink>
    </div>
  )
}

export default Home