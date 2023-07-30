import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { getAllMonthStats, getTopNumberForMonth } from '../../api/useUserApi';
import { NumSentenceResult } from '../../components/Manufacturing';
import StatsContainer from '../../components/StatsContainer';

function StatsMonth() {
    const navigate = useNavigate();
    const role = useSelector((state)=>state.userIf.role);
    const [yMList, setYMList] = useState("");
    const [yearMonth, setYearMonth] = useState("");
    const [value, setValue] = useState("");
    const [render, setRender] = useState(true);

    const allMonthStatsMutation = useMutation(getAllMonthStats, {
        onSuccess: (res)=>{
            setYMList(res.yearMonthList);
        },
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
            }
        }
    })

    const getMonthStatsMutation = useMutation(getTopNumberForMonth, {
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
        console.log("랜더링")
        if  (role === null || role == "ROLE_USER") {
            alert("접근 권한이 없습니다. 프리미엄 페이지로 이동합니다.");
            navigate("/premium");
        } else {
            allMonthStatsMutation.mutate();
        }
    }, [render])

    useEffect(()=>{
        console.log(yearMonth)
        if (yearMonth !== "") {
            getMonthStatsMutation.mutate(yearMonth);
        }
    }, [yearMonth])

    useEffect(()=>{
        setRender(!render);
    }, [value])
    
    const onClickHandler = (str) => {
        setYearMonth(str);
    }

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height: "1cm"}}>Stats Month</h1>
        {value !== "" &&(
            <button onClick={()=>{
                setValue("");
            }} style={{ width: "4cm", height: "1,5cm", marginTop: "1cm", marginBottom: "2cm"}}>이전으로 돌아가기</button>
        )}
        {value === "" ? (
            <div style={{ marginTop: "2cm"}}>
                {yMList !== "" ? (
                    yMList.map((str, index)=>{
                        return (
                            <div key={`buttons${index}`}>
                                <button onClick={()=>onClickHandler(str)} style={{ width: "3cm", height: "1cm", fontSize: "20px" }} >{str}</button>
                            </div>
                        )
                    })
                ):(<div>null</div>)}
            </div>
        ): (
            <>
                <div style={{ marginBottom: "2cm"}}>
                        {value !== "" &&(
                            <span style={{ textAlign: "center"}}>{NumSentenceResult(value.value)}</span>
                        )}
                </div>
                <StatsContainer res={value.countList}/>
            </>
        )}
    </div>
  )
}

export default StatsMonth