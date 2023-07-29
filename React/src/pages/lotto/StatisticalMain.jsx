import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { getMainTopNumber } from '../../api/useUserApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';

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
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height: "1cm" }}>Statistica Main</h1>
        {value !== "" &&(
            <>
                <div style={{ marginBottom: "2cm"}}>
                        {value !== "" &&(
                            <span style={{ textAlign: "center"}}>{NumSentenceResult(value.value)}</span>
                        )}
                </div>
                <StatsContainer res={value.countList} />
            </>
        )}
    </div>
  )
}

export default StatisticalMain