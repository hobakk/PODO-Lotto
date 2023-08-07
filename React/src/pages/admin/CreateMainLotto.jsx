import React from 'react'
import { useMutation } from 'react-query';
import { createLotto } from '../../api/useUserApi';
import { CommonStyle } from '../../components/Styles';
import { AllowOnlyAdmin } from '../../components/CheckRole';
import { useNavigate } from 'react-router-dom';

function CreateMainLotto() {
    const navigate = useNavigate();
    const setMainLottoMutation = useMutation(createLotto, {
        onSuccess: (res) => {
            alert(res.msg);
            navigate("/");
        },
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
            }
        }
    })

  return (
    <div id='recent' style={ CommonStyle }>
        <AllowOnlyAdmin />
        <h1 style={{  fontSize: "80px", height:"4.5cm"}}>Create Main Lotto</h1>
        <button onClick={()=>setMainLottoMutation.mutate()}>메인 로또 생성</button>
    </div>
  )
}

export default CreateMainLotto