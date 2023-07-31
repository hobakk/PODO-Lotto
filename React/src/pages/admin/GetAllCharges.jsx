import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { getAdminCharges, upCash } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';

function GetAllCharges() {
    const navigate = useNavigate();
    const [value, setValue] = useState([]);
    const [render, setRender] = useState(true);
    const [selectValue, setSelectValue] = useState("selectCash");
    const [searchInputValue, setSearchInputValue] = useState("");
    const [result, setResult] = useState([]);

    const getAllChargingMutation = useMutation(getAdminCharges, {
        onSuccess: (res)=>{
            setValue(res);
        },
        onError: (err)=>{
            if (err.status === 500) {
                alert(err.message);
                navigate("/");
            }
        }
    })

    const upCashMutation = useMutation(upCash, {
        onSuccess: (res)=>{
            if (res === 200) {
                setRender(!render);
            }
        }
    })
    
    useEffect(()=>{
        getAllChargingMutation.mutate();
    }, [render])

    useEffect(()=>{
        console.log(selectValue)
    }, [selectValue])

    const onClickHandler = (charg) => {
        console.log(charg);
        upCashMutation.mutate(charg);
    }

    useEffect(()=>{
        if (searchInputValue === "") {
            setResult([]);
        }
        if (value.length !== 0) {
            let filteredCharges = "";
            if (selectValue === "selectCash") {
                filteredCharges = value.filter(charg=>{
                    return charg.value.toString().includes(searchInputValue.toString());
                });
            } else {
                filteredCharges = value.filter(charg=>{
                    return charg.msg.includes(searchInputValue);
                });
            }
            setResult(filteredCharges);
        }
    }, [searchInputValue])

    const ResultContainer = ({ classType }) => {
        return(
            <div style={{ border:"2px solid black", width:"12cm", marginBottom:"5px", padding:"10px"}}>
                <div style={{ display:"flex", }}>
                    <span>userId: {classType.userId}</span>
                    <button onClick={()=>onClickHandler(classType)} style={{ marginLeft:"auto"}}>충전</button>
                </div>
                <div style={{ display:"flex"}}>
                    <span>cash: {classType.value}</span>
                    <span style={{ marginLeft:"auto"}}>msg: {classType.msg}</span>
                </div>
            </div>
        )
    }

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height:"1.5cm" }}>Get AllCharges</h1>
        <div style={{ display:"flex", flexDirection:"row", textAlign:"center"}}>
            <select id="selectOption" value={selectValue} onChange={(e)=>setSelectValue(e.target.value)} style={{ height:"0.65cm" }}>
                <option value="selectCash">cash</option>
                <option value="selectMsg">msg</option>
            </select>
            <input onChange={(e)=>setSearchInputValue(e.target.value)} placeholder='검색할 값을 입력해주세요' style={{ marginBottom:"1cm", width:"7cm", height:"0.5cm" }}/>
        </div>
        {searchInputValue === "" ? (
            value.length !== 0 && (
                value.map((charg, index)=>{
                    return (
                        <div key={`charges${index}`} style={{ display:"flex", flexWrap:"wrap", width:"42cm", justifyContent:"center"}}>
                            <ResultContainer classType={charg}/>
                        </div>
                    )
                })    
            )
        ):(
            <>
                {result.length !== 0 && (
                    result.map((charg, index)=>{
                        return (
                            <div key={`charges${index}`} style={{ display:"flex", flexWrap:"wrap", width:"42cm", justifyContent:"center"}}>
                                <ResultContainer classType={charg}/>
                            </div>
                        )
                    })
                )}
            </>
        )}
    </div>
  )
}

export default GetAllCharges