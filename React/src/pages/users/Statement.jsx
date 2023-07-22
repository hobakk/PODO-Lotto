import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getStatement } from '../../api/useUserApi'
import { useMutation } from 'react-query'

function Statement() {
    const [isAssign, setAssign] = useState(false);
    const [value, setValue] = useState([]);
    const StateMnetMutation = useMutation(getStatement, {
        onSuccess: (res)=>{
            setAssign(true);
            setValue(res);
        },
        onError: (err)=>{
            alert(err.msg);
        }
    });

    useEffect(()=>{
        if  (isAssign === false) {
            StateMnetMutation.mutate();
        }
    }, [])

    const PStyle = {
        border: "2px solid black", 
        width: "4cm", 
        height: "1cm",
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Statement</h1>
        {isAssign && (
            <div key={value}>
                {value.map((item, index)=>{
                    return (
                        <div key={item.localDate + index} style={{ marginBottom: "2px", fontSize: "20px"}}>
                            <div style={{ display: "flex"}}>
                                <p style={{...PStyle, textAlign: "center", borderRight: "0px", }}>{item.localDate}</p>
                                <p style={{...PStyle, width: "20cm"}}>
                                    <p style={{ marginLeft: "15px" }}>{item.msg}</p>
                                </p>
                            </div>
                        </div>    
                    )
                })}
            </div>
        )}
    </div>
  )
}

export default Statement