import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { getUsers } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';

function GetUsers() {
  const [value, setValue] = useState("");

  const getUsersMutation = useMutation(getUsers, {
    onSuccess: (res) =>{
      setValue(res);
    }
  })

  useEffect(()=>{
    getUsersMutation.mutate();
  },[])

  useEffect(()=>{
    console.log(value);
  }, [value])

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
                      <span style={{ marginLeft: "auto" }}>Cash: {user.cash}</span>
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