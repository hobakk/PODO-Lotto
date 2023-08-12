import React, { useEffect, useState } from 'react'
import { ChangingNumStyle } from './Manufacturing';

type StatsContainerProps = { res: string[] }

export const StatsContainer = ({ res }: StatsContainerProps) => {
    const [countList, setCountList] = useState<string[]>([]);

    useEffect(()=>{
        if (res !== null) {
            setCountList(res);
        }
    }, [res])

    const Style: React.CSSProperties = {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        width: "4cm",
        height: "1.4cm",
        border: "1px solid gray",
    }
  return (
    <div style={{ fontSize: "20px", width: "42cm" }}>
        {countList.length !== 0 && (
            <div style={{ display: "flex", flexWrap: "wrap"}}>
                {countList.map((num, index)=>{
                    return (
                        <div style={Style}>
                            {ChangingNumStyle(index + 1)}
                            <p style={{ margin: "5px" }}>:</p>
                            <p>{num}</p>             
                        </div>
                    )
                })}
            </div>
        )}
    </div>
  )
}

export default StatsContainer;