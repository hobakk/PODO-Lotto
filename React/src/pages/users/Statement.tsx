import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { getStatement } from '../../api/userApi'
import { useMutation } from 'react-query'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function Statement() {
    const [value, setValue] = useState<{localDate: string, msg: string}[]>([]);

    const StateMnetMutation = useMutation<UnifiedResponse<{localDate: string, msg: string}[]>, Err>(getStatement, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                setValue(res.data);
            }
        },
        onError: (err)=>{
            console.log(err);
        }
    });

    useEffect(()=>{
        if  (value.length === 0)
        StateMnetMutation.mutate();
    }, [])

    const PStyle: React.CSSProperties = {
        border: "2px solid black",
        borderBottom: "0px", 
        width: "4cm", 
        height: "1cm",
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Statement</h1>
        <div>
            <div style={{ display: "flex", fontSize:"22px", backgroundColor:"#D4F0F0"}}>
                <span style={{...PStyle, textAlign: "center", borderRight: "0px", }}>DATE</span>
                <span style={{...PStyle, width: "20cm"}}>
                    <p style={{ marginLeft: "15px", textAlign:"center" }}>MSG</p>
                </span>
            </div>
            {value.length !== 0 && (
                value.map((item, index)=>{
                    return (
                        <div key={item.localDate + index} style={{ fontSize: "20px"}}>
                            <div style={{ display: "flex"}}>
                                <span style={{...PStyle, textAlign: "center", borderRight: "0px", }}>{item.localDate}</span>
                                <span style={{...PStyle, width: "20cm"}}>
                                    <p style={{ marginLeft: "15px" }}>{item.msg}</p>
                                </span>
                            </div>
                        </div>   
                    )
                })
            )}
            <div style={{ borderTop: "2px solid black", width: "24cm"}} />
        </div>
    </div>
  )
}

export default Statement