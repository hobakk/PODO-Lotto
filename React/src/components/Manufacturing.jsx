import React from 'react';
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

export const NumSentenceStyle = (numSentence) => {
  return (
      <div key={`sentence-${numSentence}`} style={{ marginTop: "30px" }}>
          <div style={{ display: "flex"}}>
            {numSentence.split(" ").map((num, input)=>ChangingNumStyle(num, input))}
          </div>
      </div>
  )
}