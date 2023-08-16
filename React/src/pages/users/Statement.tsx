import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { getStatement } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { Res, errorType } from '../../shared/TypeMenu';
import { useAllowType } from '../../hooks/AllowType';

function Statement() {
    const [isAssign, setAssign] = useState<boolean>(false);
    const [value, setValue] = useState<{localDate: string, msg: string}[]>([]);
    const isAllow: boolean = useAllowType("AllowLogin");

    const StateMnetMutation = useMutation(getStatement, {
        onSuccess: (res: Res)=>{
            setAssign(true);
            setValue(res.data);
        },
        onError: (err: errorType)=>{
            alert(err.message);
        }
    });

    useEffect(()=>{
        if  (isAllow && isAssign === false) {
            StateMnetMutation.mutate();
        }
    }, [isAllow])

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
        {isAssign && (
            <div>
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