import React, { useEffect, useState } from 'react'
import { ResultContainer } from '../../components/Manufacturing'
import { useMutation } from 'react-query'
import { CommonStyle } from '../../shared/Styles';
import { getRecentNumber } from '../../api/sixNumberApi';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function RecentNumber() {
    const [value, setValue] = useState<string[]>([]);

    const getRecentNumMutation = useMutation<UnifiedResponse<string[]>, unknown>(getRecentNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        },
        onError: (err: any | Err)=>{
            if (err.status) console.log(err.message);
            else if (err.msg) console.log(err.msg);
        }
    })
    
    useEffect(()=>{ getRecentNumMutation.mutate(); }, [])

  return (
    <div>
        <div style={ CommonStyle }>
            <h3 style={{ fontSize: "80px"}}>Recent Number</h3>
            <div>
                {value.length !== 0 ? (
                    <ResultContainer numSentenceList={value} />
                ):(<div>구매 기록이 없습니다</div>)}
            </div>
        </div>
    </div>
  )
}

export default RecentNumber