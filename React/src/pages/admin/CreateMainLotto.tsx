import React from 'react'
import { useMutation } from 'react-query';
import { createLotto } from '../../api/useUserApi';
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';
import { Res, errorType } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function CreateMainLotto() {
    useAllowType(AllowOnlyAdmin);
    const navigate = useNavigate();
    const setMainLottoMutation = useMutation(createLotto, {
        onSuccess: (res: Res) => {
            alert(res.message);
            navigate("/");
        },
        onError: (err: errorType)=>{
            if (err.code === 500) {
                alert(err.message);
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