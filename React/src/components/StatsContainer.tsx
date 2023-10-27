import React, { useEffect, useState } from 'react'
import { ChangingNumStyle } from './Manufacturing';

export const StatsContainer = ({ res }: {res: number[]}) => {
    const [countList, setCountList] = useState<number[]>([]);
    const chunkSize = 5;

    useEffect(()=>{
        if (res !== null) {
            setCountList(res);
        }
    }, [res])

    const Style: React.CSSProperties = {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        textAlign: "center",
        height: "1.4cm",
        border: "1px solid gray",
    }

    return (
        <div style={{ fontSize: "20px", width: "42cm" }}>
          {countList.length !== 0 && (
            <div style={{ display: "flex", flexDirection: "column" }}>
              {Array.from({ length: 9 }).map((_, chunkIndex) => (
                <div key={chunkIndex}>
                  {countList.slice(chunkIndex * chunkSize, (chunkIndex + 1) * chunkSize).map((num, index) => (
                    <div style={Style} key={index}>
                      {ChangingNumStyle({ num: index + 1 + chunkIndex * chunkSize, index: 0 })}
                      <p style={{ margin: "5px" }}>:</p>
                      <p>{num}</p>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          )}
        </div>
      )
    }

export default StatsContainer;