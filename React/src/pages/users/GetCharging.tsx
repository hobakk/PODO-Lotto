import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { getCharges } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { Res, errorType } from '../../shared/TypeMenu';
import { AllowLogin, useAllowType } from '../../hooks/AllowType';

function GetCharging() {
  const [chargValue, setChargValue] = useState<{cash: number, msg: string}[]>([]);
  const isAllow = useAllowType(AllowLogin);
  const getChargesMutation = useMutation(getCharges, {
    onSuccess: (res: Res)=>{
      if (res.data !== null) {
        setChargValue(res.data);
      }
    },
    onError: (err: errorType)=>{
      if  (err.code === 500) {
        alert(err.message);
      }
    }
  })

  useEffect(()=>{
    if (isAllow) {
      getChargesMutation.mutate();
    }
  }, [isAllow])

  const BoxStyle: React.CSSProperties = {
    display:"flex",
    justifyContent:"center",
    textAlign:"center",
    fontSize:"22px",
    alignItems:"center",
    flexDirection:'row',
  }
  const BorderStyle: React.CSSProperties = { 
    border:"3px solid black",
    borderRight:"0px",
    borderBottom:"0px",
    width:"9cm",
    height:"1cm",
    display: "flex", 
    alignItems: "center", 
    justifyContent: "center",
  }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>getCharging</h1>
        <div style={{ ...BoxStyle, marginTop:"1cm", }}>
          <div style={{ ...BorderStyle, backgroundColor:"#D4F0F0", }}><span>MSG</span></div>
          <div style={{ ...BorderStyle, width:"14cm", backgroundColor:"#D4F0F0", }}><span >CASH</span></div>
          <div style={{ borderLeft:"3px solid black", height:"1.08cm" }} />
        </div>
        {chargValue.length !== 0 || chargValue !== null ? (
          chargValue.map(item=>{
            return (
              <div style={BoxStyle}>
                <div key={item.msg} style={BorderStyle}><span>{item.msg}</span></div>
                <div key={item.msg} style={{ ...BorderStyle, width:"14cm" }}><span>{item.cash}</span></div>
                <div style={{ borderLeft:"3px solid black", height:"1.08cm" }} />
              </div>
            )
          })
        ):null}
        <div style={{ border:"3px solid black", borderBottom:"0px", width:"23cm", }}/>
    </div>
  )
}

export default GetCharging;