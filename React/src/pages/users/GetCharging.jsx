import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getCharges } from '../../api/useUserApi'
import { useMutation } from 'react-query'

function GetCharging() {
  const [chargValue, setChargValue] = useState([]);
  const getChargesMutation = useMutation(getCharges, {
    onSuccess: (res)=>{
      if (res !== null) {
        setChargValue(res);
      } else {
        alert("충전요청이 존재하지 않습니다");
      }
    }  

  })

  useEffect(()=>{
    console.log("랜더링")
    getChargesMutation.mutate();
  }, [])

  const outputBox = {
    border: "3px solid black",
    width: "9cm",
    height: "3cm",
    fontSize: "22px",
    marginTop: "5px",
  }

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>getCharging</h1>
            {chargValue !== "" || chargValue !== null ? (
              chargValue.map(item=>{
                return (
                  <div key={item.msg} style={outputBox}>
                    <p style={{ marginLeft: "20px"}}>msg: {item.msg}</p>
                    <p style={{ marginLeft: "20px"}}>cash: {item.cash}</p>
                  </div>  
                )
              })
            ):null}
        </div>
    </div>
  )
}

export default GetCharging;