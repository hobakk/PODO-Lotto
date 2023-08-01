import React, { useEffect } from 'react'
import { useMutation } from 'react-query';
import { useSelector } from 'react-redux'
import { createLotto } from '../../api/useUserApi';
import { useNavigate } from 'react-router-dom';
import { CommonStyle } from '../../components/Styles';

function CreateMainLotto() {
    const navigate = useNavigate();
    const userRole = useSelector((state)=>state.userIf.role);

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

    useEffect(()=>{
        if (userRole !== "ROLE_ADMIN") {
            alert("접근 권한이 없습니다")
        }
    }, [userRole])

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height:"4.5cm"}}>Create Main Lotto</h1>
        <button onClick={()=>setMainLottoMutation.mutate()}>메인 로또 생성</button>
    </div>
  )
}

export default CreateMainLotto