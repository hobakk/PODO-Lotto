import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { getAdminCharges, upCash } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';

function GetAllCharges() {
    const [value, setValue] = useState([]);
    const navigate = useNavigate();
    const [render, setRender] = useState(true);

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

    const onClickHandler = (charg) => {
        console.log(charg);
        upCashMutation.mutate(charg);
    }

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Get AllCharges</h1>
        {value.length !== 0 && (
            value.map((charg, index)=>{
                return(
                    <div key={`charges${index}`} style={{ display:"flex", flexWrap:"wrap", width:"42cm", justifyContent:"center"}}>
                        <div style={{ border:"2px solid black", width:"12cm", marginBottom:"5px", padding:"10px"}}>
                            <div style={{ display:"flex", }}>
                                <span>userId: {charg.userId}</span>
                                <button onClick={()=>onClickHandler(charg)} style={{ marginLeft:"auto"}}>충전</button>
                            </div>
                            <div style={{ display:"flex"}}>
                                <span>value: {charg.value}</span>
                                <span style={{ marginLeft:"auto"}}>msg: {charg.msg}</span>
                            </div>
                        </div>
                    </div>    
                )
            })    
        )}
    </div>
  )
}

export default GetAllCharges