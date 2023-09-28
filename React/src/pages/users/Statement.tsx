import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../shared/Styles'
import { StatementModifyMsgRequest, StatementResponse, getStatement, modifyStatementMsg } from '../../api/userApi'
import { useMutation } from 'react-query'
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function Statement() {
    const [value, setValue] = useState<StatementResponse[]>([]);
    const [isModify, setIsModify] = useState<{[key: string]: boolean}>({});
    const [inputValue, setInputValue] = useState<StatementModifyMsgRequest>({
        statementId: 0,
        msg: "",
    });

    const StateMnetMutation = useMutation<UnifiedResponse<StatementResponse[]>, any>(getStatement, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                setValue(res.data);
            }
        },
        onError: (err: any)=>{
            if  (err.status) console.log(err.message);
        }
    });

    const modifyStatementMsgMutation = 
    useMutation<UnifiedResponse<undefined>, unknown, StatementModifyMsgRequest>(modifyStatementMsg, {
        onSuccess: (res)=>{
            if (res.code === 200) {
                const targetItem = value.find(item => item.statementId === inputValue.statementId);
                if (targetItem) {
                    targetItem.msg = inputValue.msg;
                    targetItem.modify = true;
                    setValue([...value]);
                }
            }
        },
        onError: (err: any | Err)=>{
            if  (err.code) alert(err.msg);
        }
    });

    useEffect(()=>{
        if  (value.length === 0)
        StateMnetMutation.mutate();
    }, []);

    useEffect(()=>console.log(value), [value]);
    useEffect(()=>console.log(inputValue), [inputValue]);

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target; 
        setInputValue({
            statementId: parseInt(name),
            msg: value,
        });
    }

    const onClickHandler = (statementId: number) => {
        if (inputValue.msg !== "") {
            modifyStatementMsgMutation.mutate(inputValue);
        } 

        setIsModify({ ...isModify, [statementId]: false })
    }

    const TableStyle: React.CSSProperties = {
        border: "2px solid black",
        borderBottom: "0px",
        borderRight: "0px",
        width: "13%", 
        height: "1cm",
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
        textAlign: "center",
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Statement</h1>
        <span style={{ marginBottom:"20px", fontSize:"18px" }} >최근 한달 동안 거래내역만 출력됩니다</span>
        <div>
            <div style={{ display: "flex", fontSize:"22px", backgroundColor:"#D4F0F0", width:"40cm"}}>
                <div style={TableStyle}>Date</div>
                <div style={{ ...TableStyle, width: "34%", }}>Subject</div>
                <div style={TableStyle}>Cash</div>
                <div style={{ ...TableStyle, width: "50%", borderRight:"2px solid black" }}>Message</div>
            </div>
            {value.length !== 0 && (
                value.map((item, index)=>(
                    <div key={item.localDate + index} style={{ display: "flex", fontSize: "20px", width:"40cm"}}>
                        <span style={TableStyle}>{item.localDate}</span>
                        <span style={{ ...TableStyle, width: "34%", }}>{item.subject}</span>
                        <span style={TableStyle}>{item.cash}</span>
                        <div style={{ ...TableStyle, width:"50%", borderRight:"2px solid black", flexDirection:"row" }}>
                            {isModify[item.statementId] ? (
                                <>
                                    <input 
                                        style={{ width:"90%" }}
                                        name={`${item.statementId}`}
                                        value={inputValue.msg}
                                        placeholder='문자를 입력해주세요'
                                        onChange={onChangeHandler} />
                                    <button 
                                        style={{ width:"10%" }} 
                                        onClick={()=>onClickHandler(item.statementId)}
                                    >
                                        완료
                                    </button>
                                </>
                            ):(
                                <>
                                    <div style={{ width:"90%" }}>
                                        <span>{item.msg}</span>
                                        {item.modify && (<span style={{ marginLeft:"5px", color:"red" }}>(수정됨)</span>)}
                                    </div>
                                    <button 
                                        style={{ width:"10%" }} 
                                        onClick={()=>setIsModify({ ...isModify, [item.statementId]: true })}
                                    >
                                        수정
                                    </button>
                                </>
                            )}
                        </div>
                    </div>   
                ))
            )}
            <div style={{ borderTop: "2px solid black", width:"40cm"}} />
            {value.length === 0 && (
                <div style={{ textAlign:"center", fontSize:"22px", marginTop:"2cm" }}>
                    <span>거래내역이 존재하지 않습니다</span>
                </div>
            )}
        </div>
    </div>
  )
}

export default Statement