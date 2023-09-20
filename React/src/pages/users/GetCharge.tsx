import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../shared/Styles'
import { ChargeResponse, getCharges } from '../../api/userApi'
import { useMutation } from 'react-query'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function GetCharge() {
  const [chargValue, setChargValue] = useState<ChargeResponse>({
    msg: "",
    cash: 0,
    date: "",
  });

  const { msg, cash, date } = chargValue;

  const getChargesMutation = useMutation<UnifiedResponse<ChargeResponse>>(getCharges, {
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
        <h1 style={{  fontSize: "80px" }}>get Charge</h1>
        <div style={{ ...BoxStyle, marginTop:"1cm", }}>
          <div style={{ ...BorderStyle, backgroundColor:"#D4F0F0", }}><span>MSG</span></div>
          <div style={{ ...BorderStyle, width:"14cm", backgroundColor:"#D4F0F0", }}><span >CASH</span></div>
          <div style={{ borderLeft:"3px solid black", height:"1.08cm" }} />
        </div>
        {msg !== "" && date !== "" ? (
          <div style={BoxStyle}>
            <div style={BorderStyle}><span>{msg}</span></div>
            <div style={{ ...BorderStyle, width:"14cm" }}><span>{cash}</span></div>
            <div style={{ borderLeft:"3px solid black", height:"1.08cm" }} />
          </div>
        ):null}
        <div style={{ border:"3px solid black", borderBottom:"0px", width:"23cm", }}/>
    </div>
  )
}

export default GetCharge;