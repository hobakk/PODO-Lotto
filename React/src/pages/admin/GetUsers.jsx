import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { downCash, getUsers } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { Contains } from '../../components/Manufacturing';

function GetUsers() {
  const [cash, setCash] = useState({});
  const [value, setValue] = useState([]);
  const [render, setRender] = useState(true);
  const [searchInputValue, setSearchInputValue] = useState("");
  const [result, setResult] = useState([]);

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
    if (searchInputValue === "") {
      setResult([]);
    }
    if (value.length !== 0) {
      const filteredUsers = value.filter(
        (user) =>
          (user.email.includes(searchInputValue) ||
            user.nickname.includes(searchInputValue))
      );
      setResult(filteredUsers);
    }
  }, [searchInputValue])

  const onClickHandler = (userId) => {
    const CashRequest = {
      userId,
      value: cash[userId],
      msg: "문의하세요"
    }

    downCashMutation.mutate(CashRequest);
  }

  const onChangeHandler = (e, userId) => {
    setCash({
      ...cash,
      [userId]: e.target.value,
    })
  }

  const MapFirstStyle = {
    display: 'flex',
    width: "42cm",
    alignContent: "center",
    justifyContent: "center",
  }
  const MapSecondStyle = {
    display: "flex",
    flexDirection: "column",
    border: "3px solid black",
    width: "12cm",
    marginBottom: "5px"
  }

  return (

    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height:"1.5cm"}}>Get Users</h1>
        <input onChange={(e)=>setSearchInputValue(e.target.value)} placeholder='검색할 값을 입력해주세요' style={{ marginBottom:"1cm", width:"7cm", height:"0.5cm" }}/>
        {searchInputValue === "" ? (
            value.filter(user=>user.role !== "ROLE_ADMIN").map(user=>{
              return (
                <div key={user.id} style={MapFirstStyle}>
                  <div style={MapSecondStyle}>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>id: {user.id}</span>
                      <span style={{ margin: "auto" }}>Cash: {user.cash}</span>
                      <input name={`${user.id}`} onChange={(e)=>onChangeHandler(e, user.id)} />
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
        ):(
          <div style={MapFirstStyle}>
            {result.length === 0 ? (
              <div>검색 결과 없음</div>
            ):(
              result.map(item=>{
                return(
                  <div style={MapSecondStyle}>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>id: {item.id}</span>
                      <span style={{ margin: "auto" }}>Cash: {item.cash}</span>
                      <input name={`${item.id}`} onChange={(e)=>onChangeHandler(e, item.id)} />
                      <button onClick={()=>onClickHandler(item.id)}>포인트 차감</button>
                    </div>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>Email: {item.email}</span>
                      <span style={{ marginLeft: "auto" }}>Nickname: {item.nickname}</span>
                    </div>
                    <div style={{ display:"flex", padding: "10px" }}>
                      <span>Role: {item.role}</span>
                      <span style={{ marginLeft: "auto" }}>Status: {item.status}</span>
                    </div>
                  </div>
                )
              })
            )}
          </div>
        )}
    </div>
  )
}

export default GetUsers