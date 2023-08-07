import React, { useEffect, useState } from 'react'
import { ResultContainer } from '../../components/Manufacturing'
import { useMutation } from 'react-query'
import { CommonStyle } from '../../components/Styles';
import { getRecentNumber } from '../../api/useUserApi';
import { AllowAll } from '../../components/CheckRole';

function RecentNumber() {
    const [value, setValue] = useState("");

    const getRecentNumMutation = useMutation(getRecentNumber, {
        onSuccess: (res)=>{
            setValue(res);
        },
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
            }
        } 
    })
    
    useEffect(()=>{
        getRecentNumMutation.mutate();
    }, [])

  return (
    <div>
        <AllowAll />
        <div style={ CommonStyle }>
            <h3 style={{ fontSize: "80px"}}>Recent Number</h3>
            <div>
                {value !== "" ? (
                    <ResultContainer numSentenceList={value} />
                ):(<div>구매 기록이 없습니다</div>)}
            </div>
        </div>
    </div>
  )
}

export default RecentNumber