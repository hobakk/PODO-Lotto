import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../shared/Styles'
import { ChargingDto, getCharges } from '../../api/userApi'
import { useMutation } from 'react-query'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function GetCharging() {
  const [chargValue, setChargValue] = useState<ChargingDto[]>([]);
  const getChargesMutation = useMutation<UnifiedResponse<ChargingDto[]>>(getCharges, {
    onSuccess: (res)=>{
      if (res.code === 200 && res.data) {
        setChargValue(res.data);
      }
    },
    onError: (err: any)=>{
      if  (err.status) console.log(err.message);
    }
  })

  useEffect(()=>{ getChargesMutation.mutate(); }, [])

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
              <div key={`${item.msg}-${item.cash}`} style={BoxStyle}>
                <div style={BorderStyle}><span>{item.msg}</span></div>
                <div style={{ ...BorderStyle, width:"14cm" }}><span>{item.cash}</span></div>
                <div style={{ borderLeft:"3px solid black", height:"1.08cm" }} />
              </div>
            )
          })
        ):null}
        <div style={{ border:"3px solid black", borderBottom:"0px", width:"23cm", }}/>
        {chargValue.length === 0 && (
          <div style={{ textAlign:"center", fontSize:"22px", marginTop:"2cm" }}>
              <span>충전요청이 존재하지 않습니다</span>
          </div>
        )}
    </div>
  )
}

export default GetCharging;