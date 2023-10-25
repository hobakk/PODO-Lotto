import React, { useEffect, useState } from 'react'
import { ButtonDiv, ButtonStyle, CommonStyle, TitleStyle } from '../../shared/Styles'
import { ChargeResponse, deleteCharge, getCharges } from '../../api/userApi'
import { useMutation } from 'react-query'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function GetCharge() {
  const initialData = {
    msg: "",
    cash: 0,
    date: "",
  };

  const [chargValue, setChargValue] = useState<ChargeResponse>({
    ...initialData
  });

  const { msg, cash, date } = chargValue;

  const getChargesMutation = useMutation<UnifiedResponse<ChargeResponse>>(getCharges, {
    onSuccess: (res)=>{
      if (res.code === 200 && res.data) {
        setChargValue(res.data);
      }
    },
    onError: (err: any)=>{
      if  (err.status) console.log(err.message);
    }
  });

  const deleteChargeMutation = useMutation<UnifiedResponse<undefined>, undefined, string>(deleteCharge);

  useEffect(()=>{ getChargesMutation.mutate(); }, []);

  const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const key = msg + "-" + cash;
    deleteChargeMutation.mutate(key);
    setChargValue(initialData);
  }

  const BoxStyle: React.CSSProperties = {
    width: "18cm",
    height: "6cm",
    border: "2px solid black",
    padding:"10px",
    fontSize:"22px",
    backgroundColor: "#D4F0F0",
  }

  const inDivStyle: React.CSSProperties = {
    display:"flex", 
    justifyItems:"center",
    marginBottom:"20px",
  }

  return (
    <div style={ CommonStyle }>
        <h1 style={ TitleStyle }>충전 요청 확인</h1>
        {msg !== "" && date !== "" ? (
          <div style={BoxStyle}>
            <div style={inDivStyle}>
              <div style={{ marginLeft:"auto" }}>
                만료: <span style={{ marginLeft:"20px", color:"blue" }}>{date}</span>
              </div>
            </div>
            <div style={{ backgroundColor:"white", padding:"10px", width:"16.5cm", margin:"auto" }}>
              <div style={inDivStyle}>
                <span>입금 메세지:</span>
                <span style={{ marginLeft:"auto" }}>{msg}</span>
              </div>
              <div style={inDivStyle}>
                <span>금액:</span>
                <span style={{ marginLeft:"auto" }}>{cash}</span>
              </div>
            </div>
            <form onSubmit={onSubmitHandler} style={{ marginLeft: "auto", width: "5cm", marginTop:"20px"}}>
              <button style={{ marginLeft: "auto", width: "100%", height:"30px" }}>삭제하기</button>
            </form>
          </div>
        ):(
          <div style={{ textAlign:"center", fontSize:"22px", marginTop:"2cm" }}>
            <span>거래내역이 존재하지 않습니다</span>
          </div>
        )}
    </div>
  )
}

export default GetCharge;