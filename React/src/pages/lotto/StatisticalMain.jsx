import React, { useEffect, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { getMainTopNumber } from '../../api/useUserApi';

function StatisticalMain() {
    const navigate = useNavigate();
    const role = useSelector((state)=>state.userIf.role);
    const [value, setValue] = useState("");

    const MainMutation = useMutation(getMainTopNumber, {
        onSuccess: (res)=>{
            setValue(res);
        }
    })

    useEffect(()=>{
        if  (role === null || role == "ROLE_USER") {
            alert("접근 권한이 없습니다. 프리미엄 페이지로 이동합니다.");
            navigate("/premium");
        } else {
            MainMutation.mutate();
        }
    }, [])

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>Statistica lMain</h1>
            {value !== "" ? (value.map()):null }
        </div>
    </div>
  )
}

export default StatisticalMain