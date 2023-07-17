import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { getCharges } from '../../api/useUserApi'
import { useMutation } from 'react-query'

function GetCharging() {
  const [chargValue, setChargValue] = useState("");
  const getChargesMutation = useMutation(getCharges, {
    onSuccess: (res)=>{
      if (res !== null) {
        setChargValue(res);
      } 
    }  
  })

  useEffect(()=>{
    if  (chargValue === null) {
      getChargesMutation.mutate();
    }
  }, [])

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>getCharging</h1>

        </div>
    </div>
  )
}

export default GetCharging;