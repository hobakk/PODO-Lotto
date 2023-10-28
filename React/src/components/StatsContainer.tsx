import React, { useEffect, useState } from 'react'
import { ChangingNumStyle } from './Manufacturing';

export const StatsContainer = ({ res }: {res: number[]}) => {
    const [countList, setCountList] = useState<number[]>([]);

    useEffect(()=>{
        if (res !== null) {
            setCountList(res);
        }
    }, [res])

    const Style: React.CSSProperties = {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        width: "5cm",
        height: "1.4cm",
    }

  return (
    <div style={{ fontSize: "20px", width: "28cm" }}>
        {countList.length !== 0 && (

            <div style={{ display: "flex", flexWrap: "wrap"}}>
                {countList.map((num, index)=> (
                    <div style={ Style }>
                        {ChangingNumStyle({num: index + 1, index: 0})}
                        <p style={{ margin: "5px" }}>:</p>
                        <p style={{ margin:"auto" }}>{num}</p>             
                    </div>
                ))}
            </div>

        )}
    </div>
  )
}

export default StatsContainer;