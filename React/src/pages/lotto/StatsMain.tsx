import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { getMainTopNumber } from '../../api/useUserApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';
import { Res } from '../../shared/TypeMenu';
import { AllowNotRoleUser, useAllowType } from '../../hooks/AllowType';

function StatsMain() {
    const [value, setValue] = useState<{countList: string[], value: string}>({countList: [], value: ""});
    const isAllow = useAllowType(AllowNotRoleUser);

    const MainMutation = useMutation(getMainTopNumber, {
        onSuccess: (res: Res)=>{
            setValue(res.data);
        }
    })

    useEffect(()=>{
        if (isAllow) {
            MainMutation.mutate();
        }
    }, [isAllow])

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height: "1cm" }}>Stats Main</h1>
        <div style={{ marginBottom: "2cm"}}>
                {value.value !== "" &&(
                    <span style={{ textAlign: "center"}}>{NumSentenceResult(value.value)}</span>
                )}
        </div>
        {value.countList.length !== 0 &&(
            <StatsContainer res={value.countList} />
        )}
    </div>
  )
}

export default StatsMain