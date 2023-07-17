import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getStatement } from '../../api/useUserApi'
import { useMutation } from 'react-query'

function Statement() {
    const [isAssign, setAssign] = useState(false);
    const [value, setValue] = useState("");
    const StateMnetMutation = useMutation(getStatement, {
        onSuccess: (res)=>{
            setAssign(true);
            setValue(res);
        }
    });

    useEffect(()=>{
        if  (isAssign === false) {
            StateMnetMutation.mutate();
        }
    }, [])

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>Statement</h1>
            {isAssign && (
                <div key={value} style={{ border: "3px solid black", width: "10cm", height: "3cm", textAlign: "center" }}>
                    {value.map()}
                </div>
            )}
        </div>
    </div>
  )
}

export default Statement