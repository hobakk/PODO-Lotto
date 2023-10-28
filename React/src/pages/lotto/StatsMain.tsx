import React, { useEffect, useState } from 'react'
import { CommonStyle, TitleStyle } from '../../shared/Styles'
import { useMutation } from 'react-query';
import { LottoResponse, getMainTopNumber } from '../../api/lottoApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';

function StatsMain() {
    const [value, setValue] = useState<LottoResponse>({countList: [], value: ""});

    const MainMutation = useMutation<UnifiedResponse<LottoResponse>, any>(getMainTopNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        },
        onError: (err)=>{
            if (err.status !== 500) alert(err.message);
        }
    })

    useEffect(()=>{ 
        if (value.countList.length === 0 && value.value === "") {
            MainMutation.mutate(); 
        }
    }, [])

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{ fontSize: "40px", marginBottom: "1cm" }}>사이트 통계</h1>
        <div style={{ marginBottom: "2cm"}}>
                {value.value !== "" &&(
                    <span style={{ textAlign: "center"}}>
                        <NumSentenceResult numSentence={value.value} />
                    </span>
                )}
        </div>
        {value.countList.length !== 0 &&(
            <StatsContainer res={value.countList} />
        )}
    </div>
  )
}

export default StatsMain