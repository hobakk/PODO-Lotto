import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { getAdminCharges, upCash } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';
import { AdminGetCharges, Res, errorType } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function GetAllCharges() {
    useAllowType(AllowOnlyAdmin);
    const navigate = useNavigate();
    const [value, setValue] = useState<AdminGetCharges[]>([]);
    const [render, setRender] = useState<boolean>(true);
    const [selectValue, setSelectValue] = useState<string>("selectCash");
    const [searchInputValue, setSearchInputValue] = useState<string>("");
    const [result, setResult] = useState<AdminGetCharges[]>([]);

    const getAllChargingMutation = useMutation(getAdminCharges, {
        onSuccess: (res: Res)=>{
            setValue(res.data);
        },
        onError: (err: errorType)=>{
            if (err.code === 500) {
                alert(err.message);
                navigate("/");
            }
        }
    })

    const upCashMutation = useMutation(upCash, {
        onSuccess: (res: Res)=>{
            if (res.code === 200) {
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

    const onClickHandler = (charg: AdminGetCharges) => {
        console.log(charg);
        upCashMutation.mutate(charg);
    }

    useEffect(()=>{
        if (searchInputValue === "") {
            setResult([]);
        }
        if (value.length !== 0) {
            let filteredCharges: AdminGetCharges[] = [];
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

    const ResultContainer = ({ chargProp }: { chargProp: AdminGetCharges }) => {
        return(
            <div style={{ border:"2px solid black", width:"12cm", marginBottom:"5px", padding:"10px"}}>
                <div style={{ display:"flex", }}>
                    <span>userId: {chargProp.userId.toString()}</span>
                    <button onClick={()=>onClickHandler(chargProp)} style={{ marginLeft:"auto"}}>충전</button>
                </div>
                <div style={{ display:"flex"}}>
                    <span>cash: {chargProp.value}</span>
                    <span style={{ marginLeft:"auto"}}>msg: {chargProp.msg}</span>
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
                value.map((charg: AdminGetCharges, index: number)=>{
                    return (
                        <div key={`charges${index}`} style={{ display:"flex", flexWrap:"wrap", width:"42cm", justifyContent:"center"}}>
                            <ResultContainer chargProp={charg}/>
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
                                <ResultContainer chargProp={charg}/>
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