import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { downCash, getUsers } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';

function GetUsers() {
  const [cash, setCash] = useState([]);
  const [value, setValue] = useState("");
  const [render, setRender] = useState(true);

  const getUsersMutation = useMutation(getUsers, {
    onSuccess: (res) =>{
      setValue(res);
    }
  })

  const downCashMutation = useMutation(downCash, {
    onSuccess: (res)=>{
      if  (res === 200) {
        alert("차감완료");
        setRender(!render);
      }
    },
    onError: (err)=>{
      if  (err.status === 500) {
        alert(err.message);
      }
    }
  })

  useEffect(()=>{
    getUsersMutation.mutate();
  },[render])

  useEffect(()=>{
    console.log(cash);
  }, [cash])

  const onClickHandler = (userId) => {
    const CashRequest = {
      userId,
      value: cash[userId],
      msg: "문의하세요"
    }

    console.log(CashRequest)
    downCashMutation.mutate(CashRequest);
  }

  const onChangeHandler = (e, userId) => {
    setCash({
      ...cash,
      [userId]: e.target.value,
    })
  }

  return (

    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>get Users</h1>
        {value !== "" &&(
            value.filter(user=>user.role !== "ROLE_ADMIN").map(user=>{
              return (
                <div key={user.id} style={{ display: 'flex', width: "42cm", alignContent: "center", justifyContent: "center"}}>
                  <div style={{ display: "flex", flexDirection: "column", border: "3px solid black", width: "12cm", marginBottom: "5px"}}>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>id: {user.id}</span>
                      <span style={{ margin: "auto" }}>Cash: {user.cash}</span>
                      <input name={`${user.id}`} onChange={(e)=>onChangeHandler(e, user.id)}></input>
                      <button onClick={()=>onClickHandler(user.id)}>포인트 차감</button>
                    </div>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>Email: {user.email}</span>
                      <span style={{ marginLeft: "auto" }}>Nickname: {user.nickname}</span>
                    </div>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>Role: {user.role}</span>
                      <span style={{ marginLeft: "auto" }}>Status: {user.status}</span>
                    </div>
                  </div>  
                </div>
              )
            })
        )}
    </div>
  )
}

export default GetUsers