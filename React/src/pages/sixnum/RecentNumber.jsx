import React, { useEffect, useState } from 'react'
import { ResultContainer } from '../../components/Manufacturing'
import { useMutation } from 'react-query'
import { CommonStyle } from '../../components/Styles';
import { getRecentNumber } from '../../api/useUserApi';

function RecentNumber() {
    const [value, setValue] = useState("");

    const getRecentNumMutation = useMutation(getRecentNumber, {
        onSuccess: (res)=>{
            console.log(res);
            setValue(res);
        },
        onError: (err)=>{
            alert(err.message);
        } 
    })
    
    useEffect(()=>{
        getRecentNumMutation.mutate();
    }, [])

  return (
    <div>
        <div style={ CommonStyle }>
            <h3 style={{ fontSize: "80px"}}>Login</h3>
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