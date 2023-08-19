import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query'
import { downCash, getUsers, setRoleFromAdmin, setStatusFromAdmin, setAdmin } from '../../api/useUserApi'
import { CommonStyle } from '../../components/Styles';
import { useNavigate } from 'react-router-dom';
import { Res, UserAllIf, errorType } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function GetUsers() {
  useAllowType(AllowOnlyAdmin);
  const navigate = useNavigate();
  const [cash, setCash] = useState<{ [key: number]: number }>({});
  const [value, setValue] = useState<UserAllIf[]>([]);
  const [render, setRender] = useState<boolean>(true);
  const [searchInputValue, setSearchInputValue] = useState<string>("");
  const [role, setRole] = useState<{ [key: string]: string }>({});
  const [result, setResult] = useState<UserAllIf[]>([]);
  const [status, setStatus] = useState<{ [key: number]: string }>({});
  const [key, setkey] = useState<{ [key: number]: string }>({});

  const getUsersMutation = useMutation(getUsers, {
    onSuccess: (res: Res) =>{
      setValue(res.data);
    }
  })

  const downCashMutation = useMutation(downCash, {
    onSuccess: (res: Res)=>{
      if  (res.code === 200) {
        alert("차감완료");
        setRender(!render);
      }
    },
    onError: (err: errorType)=>{
      if  (err.code === 500) {
        alert(err.message);
      }
    }
  })

  const setRoleMutation = useMutation(setRoleFromAdmin, {
    onSuccess: (res: Res)=>{
      if (res.code === 200) {
        setRender(!render);
      }
    },
    onError: (err: errorType)=>{
      if (err.code === 500) {
        alert(err.message);
      }
    }
  })

  const setStatusMutation = useMutation(setStatusFromAdmin, {
    onSuccess: (res: Res)=>{
      if (res.code === 200) {
        setRender(!render);
      }
    },
    onError: (err: errorType)=>{
      if (err.message) {
        alert(err.message);
      } 
    }
  })

  const setAdminMutation = useMutation(setAdmin, {
    onSuccess: (res: Res)=>{
      if (res.code === 200) {
        setRender(!render);
      }
    },
    onError: (err: errorType)=>{
      if (err.code === 500) {
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

  const onClickHandler = (userId: number) => {
    const CashRequest = {
      userId,
      value: cash[userId],
      msg: "문의하세요"
    }
    console.log(CashRequest)
    downCashMutation.mutate(CashRequest);
  }

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCash({
      ...cash,
      [name]: parseInt(value),
    })
  }
  const selectOnChangeHandler = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = e.target;
    setRole({
      ...role,
      [name]: value,
    })
  }
  const statusOnChangeHandler = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = e.target;
    setStatus({
      ...status,
      [name]: value,
    })
  }
  const keyOnChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setkey({
      ...key,
      [name]: value,
    })
  }

  const roleOnClickHandler = (userId: number) => {
    if (role[userId] === "ADMIN") {
      navigate("/");
    } else {
      const msg = role[userId];
      setRoleMutation.mutate({ userId, msg });
    }
  }
  const statusOnClickHandler = (userId: number) => {
    const msg = status[userId];
    setStatusMutation.mutate({ userId, msg });
  }
  const securityKeyOnClickHandler = (userId: number) => {
    const msg = key[userId];
    setAdminMutation.mutate({ userId, msg });
  }

  const MapFirstStyle: React.CSSProperties = {
    display: 'flex',
    width: "42cm",
    alignContent: "center",
    justifyContent: "center",
  }
  const MapSecondStyle: React.CSSProperties = {
    display: "flex",
    flexDirection: "column",
    border: "3px solid black",
    width: "12cm",
    marginBottom: "5px"
  }

  const ResultContainer = ({ entityType }: { entityType: UserAllIf} ) => {
    return (
      <div style={MapSecondStyle}>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>id: {entityType.id}</span>
          <span style={{ margin: "auto" }}>Cash: {entityType.cash}</span>
          <input 
            name={`${entityType.id}`}
            value={cash[entityType.id]}
            onChange={onChangeHandler}
          />
          <button onClick={()=>onClickHandler(entityType.id)}>포인트 차감</button>
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Email: {entityType.email}</span>
          <span style={{ marginLeft: "auto" }}>Nickname: {entityType.nickname}</span>
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Role: {entityType.role.split("_")[1]}</span>
          <select value={role[entityType.id]} onChange={selectOnChangeHandler} name={`${entityType.id}`} style={{ marginLeft: "auto" }}>
            <option value="USER">USER</option>
            <option value="PAID">PAID</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          {role[entityType.id] !== "ADMIN" ? (
            <button onClick={()=>roleOnClickHandler(entityType.id)}>수정하기</button>
          ):(
            // input box 에 문자, 숫자등 1개씩만 값이 타이핑됨
            <> 
              <input 
                value={key[entityType.id]} 
                name={`${entityType.id}`}
                onChange={keyOnChangeHandler} 
                placeholder='Key 입력'
              />
              <button onClick={()=>securityKeyOnClickHandler(entityType.id)}>관리자 지정</button>
            </>
          )}
        </div>
        <div style={{ display:"flex", padding: "10px" }}>
          <span>Status: {entityType.status}</span>
          <select value={status[entityType.id]} onChange={statusOnChangeHandler} style={{ marginLeft: "auto" }}>
            <option value="ACTIVE">ACTIVE</option>
            <option value="SUSPENDED">SUSPENDED</option>
            <option value="DORMANT">DORMANT</option>
          </select>
          <button onClick={()=>statusOnClickHandler(entityType.id)}>수정하기</button>
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