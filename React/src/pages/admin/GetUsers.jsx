import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { downCash, getUsers, setRoleFromAdmin } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';

function GetUsers() {
  const navigate = useNavigate();
  const [cash, setCash] = useState({});
  const [value, setValue] = useState([]);
  const [render, setRender] = useState(true);
  const [searchInputValue, setSearchInputValue] = useState("");
  const [role, setRole] = useState({});
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

  const setRoleMutation = useMutation(setRoleFromAdmin, {
    onSuccess: (res)=>{
      if (res === 200) {
        setRender(!render);
      }
    },
    onError: (err)=>{
      if (err.status === 500) {
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
  const selectOnChangeHandler = (e, userId) => {
    setRole({
      ...role,
      [userId]: e.target.value,
    })
  }

  const roleOnClickHandler = (userId) => {
    if (role[userId] === "ADMIN") {
      navigate("/");
    } else {
      const msg = role[userId];
      setRoleMutation.mutate({ userId, msg });
    }
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

  const ResultContainer = ({ entityType }) => {
    return (
      <div style={MapSecondStyle}>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>id: {entityType.id}</span>
          <span style={{ margin: "auto" }}>Cash: {entityType.cash}</span>
          <input name={`${entityType.id}`} onChange={(e)=>onChangeHandler(e, entityType.id)} />
          <button onClick={()=>onClickHandler(entityType.id)}>포인트 차감</button>
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Email: {entityType.email}</span>
          <span style={{ marginLeft: "auto" }}>Nickname: {entityType.nickname}</span>
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Role: {entityType.role}</span>
          <select value={role[entityType.id]} onChange={(e)=>selectOnChangeHandler(e, entityType.id)} style={{ marginLeft: "auto" }}>
            <option value="USER">USER</option>
            <option value="PAID">PAID</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <button onClick={()=>roleOnClickHandler(entityType.id)}>수정하기</button>
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Status: {entityType.status}</span>
          <select style={{ marginLeft: "auto" }}>

          </select>
          <button>수정하기</button>
        </div>
      </div>  
    )
  }

  return (
    <div id='recent' style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", height:"1.5cm"}}>Get Users</h1>
        <input onChange={(e)=>setSearchInputValue(e.target.value)} placeholder='검색할 값을 입력해주세요' style={{ marginBottom:"1cm", width:"7cm", height:"0.5cm" }}/>
        {searchInputValue === "" ? (
            value.filter(user=>user.role !== "ROLE_ADMIN").map(user=>{
              return (
                <div key={user.id} style={MapFirstStyle}>
                  <ResultContainer entityType={user} />
                </div>
              )
            })
        ):(
          <>
            {result.length === 0 ? (
              <div>검색 결과 없음</div>
            ):(
              result.map(item=>{
                return (
                  <div key={item.id} style={MapFirstStyle}>
                    <ResultContainer entityType={item} />
                  </div>
                )
              })
            )}
          </>
        )}
    </div>
  )
}

export default GetUsers