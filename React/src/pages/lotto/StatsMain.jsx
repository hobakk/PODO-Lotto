import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { getMainTopNumber } from '../../api/useUserApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';
import { AllowNotRoleUser } from '../../components/CheckRole';

function StatsMain() {
    const [value, setValue] = useState("");

    const MainMutation = useMutation(getMainTopNumber, {
        onSuccess: (res)=>{
            setValue(res);
        }
    })

    useEffect(()=>{
        MainMutation.mutate();
    }, [])

  return (
    <div id='recent' style={ CommonStyle }>
        <AllowNotRoleUser />
        <h1 style={{  fontSize: "80px", height: "1cm" }}>Stats Main</h1>
        {value !== "" &&(
            <>
                <div style={{ marginBottom: "2cm"}}>
                        {value !== "" &&(
                            <span style={{ textAlign: "center"}}>{NumSentenceResult(value.value)}</span>
                        )}
                </div>
                <StatsContainer res={value.countList} />
            </>
        )}
    </div>
  )
}

export default StatsMain