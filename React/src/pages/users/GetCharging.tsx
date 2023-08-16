import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getCharges } from '../../api/useUserApi'
import { useMutation } from 'react-query'
import { Res, errorType } from '../../shared/TypeMenu';
import { useAllowType } from '../../hooks/AllowType';

function GetCharging() {
  const [chargValue, setChargValue] = useState<{cash: number, msg: string}[]>([]);
  const isAllow = useAllowType("AllowLogin");
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

  const outputBox: React.CSSProperties = {
    border: "3px solid black",
    width: "9cm",
    height: "3cm",
    fontSize: "22px",
    marginTop: "5px",
  }

  return (
      <div style={ CommonStyle }>
          <h1 style={{  fontSize: "80px" }}>getCharging</h1>
          {chargValue.length !== 0 || chargValue !== null ? (
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
  )
}

export default GetCharging;