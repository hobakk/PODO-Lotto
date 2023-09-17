import React, { useEffect, useState } from 'react';
import { WinNumberStyle } from "../shared/Styles";

export const ChangingNumStyle = ({ num, index }: {num: number, index: number}) => {
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

export const NumSentenceResult = ({ numSentence }: { numSentence: string }) => {
  return (
      <div key={`sentence-${numSentence}`} style={{ marginTop: "30px" }}>
          <div style={{ display: "flex"}}>
            {numSentence.split(" ").map((num, index)=>(
              <ChangingNumStyle key={`num-${index}`} num={parseInt(num)} index={index} />
            ))}
          </div>
      </div>
  )
}

export const ResultContainer = ({numSentenceList}: {numSentenceList: string[]}) => {
  const [firstLine, setFirst] = useState<string[]>([]);
  const [secondLine, setSecond] = useState<string[]>([]);
  const [thirdLine, setThird] = useState<string[]>([]);
  
  useEffect(()=>{
    if (numSentenceList.length > 0) {
      setFirst(numSentenceList.slice(0, 8));
      setSecond(numSentenceList.slice(8, 16));
      setThird(numSentenceList.slice(16, 24));
    }
  }, [numSentenceList])

  const LineStyle = ({line}: {line: string[]}) => {
    const Style: React.CSSProperties = {
      display: "flex",
      flexWrap: "wrap"
    }

    return (
      <div>
        {line.map((numList, index) => (
              <div key={`${line}${index}`} style={Style}>
                <NumSentenceResult key={`num-sentence-${index}`} numSentence={numList} />
              </div>
            ))
        }
      </div>  
    )
  }

  return (
    <div id='resultcontent' style={{ display: "flex" }}>
      <div><LineStyle line={firstLine}/></div>
      {secondLine.length > 0 && (
          <div style={{ marginLeft: "70px" }}><LineStyle line={secondLine}/></div>
      )}
      {thirdLine.length > 0 && (
          <div style={{ marginLeft: "70px" }}><LineStyle line={thirdLine}/></div>
      )}
    </div>
)}