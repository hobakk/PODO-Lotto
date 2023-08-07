import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getStatement } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { AllowAll } from '../../components/CheckRole';

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
        borderBottom: "0px", 
        width: "4cm", 
        height: "1cm",
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
    }

  return (
    <div style={ CommonStyle }>
        <AllowAll />
        <h1 style={{  fontSize: "80px" }}>Statement</h1>
        {isAssign && (
            <div key={value}>
                {value.map((item, index)=>{
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
                })}
                <div style={{ borderTop: "2px solid black", width: "24cm"}} />
            </div>
        )}
    </div>
  )
}

export default Statement