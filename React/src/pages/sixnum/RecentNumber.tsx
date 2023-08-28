import React, { useEffect, useState } from 'react'
import { ResultContainer } from '../../components/Manufacturing'
import { useMutation } from 'react-query'
import { CommonStyle } from '../../components/Styles';
import { getRecentNumber } from '../../api/sixNumberApi';
import { UnifiedResponse, Err, SixNumber } from '../../shared/TypeMenu';
import { AllowLogin, useAllowType } from '../../hooks/AllowType';

function RecentNumber() {
    const [value, setValue] = useState<string[]>([]);
    const isAllow = useAllowType(AllowLogin);

    const getRecentNumMutation = useMutation<UnifiedResponse<string[]>, Err>(getRecentNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        },
        onError: (err)=>{
            if (err.code === 500) {
                alert(err.msg);
            }
        } 
    })
    
    useEffect(()=>{
        if (isAllow) {
            getRecentNumMutation.mutate();
        }
    }, [isAllow])

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