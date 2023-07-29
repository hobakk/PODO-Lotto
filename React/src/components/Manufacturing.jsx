import React, { useEffect, useState } from 'react';
import { WinNumberStyle } from "./Styles";

export const ChangingNumStyle = (num, index) => {
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

export const NumSentenceResult = (numSentence) => {
  return (
      <div key={`sentence-${numSentence}`} style={{ marginTop: "30px" }}>
          <div style={{ display: "flex"}}>
            {numSentence.split(" ").map((num, input)=>ChangingNumStyle(num, input))}
          </div>
      </div>
  )
}

export const ResultContainer = ({ numSentenceList }) => {
  const [firstLine, setFirst] = useState([]);
  const [secondLine, setSecond] = useState([]);
  const [thirdLine, setThird] = useState([]);
  
  useEffect(()=>{
    if (numSentenceList.length > 0) {
      setFirst(numSentenceList.slice(0, 8));
      setSecond(numSentenceList.slice(8, 16));
      setThird(numSentenceList.slice(16, 24));
    }
  }, [numSentenceList])

  return (
    <div id='resultcontent' style={{ display: "flex" }}>
      <div>
          {firstLine.map(numList=>{
              return <div style={{ display: "flex", flexWrap: "wrap",}}>{NumSentenceResult(numList)}</div>;
          })}
      </div>
      {secondLine.length > 0 && (
          <div style={{ marginLeft: "70px" }}>
              {secondLine.map(numList=>{
                  return <div style={{ display: "flex", flexWrap: "wrap",}}>{NumSentenceResult(numList)}</div>
              })}
          </div>
      )}
      {thirdLine.length > 0 && (
          <div style={{ marginLeft: "70px" }}>
              {secondLine.map(numList=>{
                  return <div style={{ display: "flex", flexWrap: "wrap",}}>{NumSentenceResult(numList)}</div>
              })}
          </div>
      )}
    </div>
)}