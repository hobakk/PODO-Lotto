import React from 'react'
import { useMutation } from 'react-query';
import { createLotto } from '../../api/adminApi';
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function CreateMainLotto() {
    useAllowType(AllowOnlyAdmin);
    const navigate = useNavigate();
    const setMainLottoMutation = useMutation<UnifiedResponse<undefined>, Err>(createLotto, {
        onSuccess: (res) => {
            alert(res.msg);
            navigate("/");
        },
        onError: (err)=>{
            if (err.code === 500) {
                alert(err.msg);
            }
        }
    })

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height:"4.5cm"}}>Create Main Lotto</h1>
        <button onClick={()=>setMainLottoMutation.mutate()}>메인 로또 생성</button>
    </div>
  )
}

export default CreateMainLotto