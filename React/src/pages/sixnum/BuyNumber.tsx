import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { buyNumber } from '../../api/sixNumberApi';
import { useMutation } from 'react-query';
import GetUserIfMutation from '../../components/GetUserIfMutation';
import { ResultContainer } from '../../components/Manufacturing';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { AllowLogin, useAllowType } from '../../hooks/AllowType';

function BuyNumber() {
    const [num, setNum] = useState<number>(0);
    const [value, setValue] = useState<string[]>([]);
    const [isEmpty, setData] = useState<boolean>(true);
    const numRef = useRef<HTMLInputElement>(null);
    const getUserIfMutation = GetUserIfMutation();
    useAllowType(AllowLogin);

    const InputStyle: React.CSSProperties = {
        width: "5cm",
        height: "25px",
    }
    const buttonStyle: React.CSSProperties = {
        width: "30px",
        height: "30px",
    }
    
    useEffect(()=>{ 
        if (numRef.current) {
            numRef.current.focus();
        }
    }, [])

    const buyNumberMutation = useMutation<UnifiedResponse<string[]>, Err, number>(buyNumber, {
        onSuccess: (res)=>{
            if  (res.code === 200 && res.data) {
                setValue(res.data);
                setData(false);
                getUserIfMutation.mutate();
            }
        },
        onError: (err)=>{
            if (err.msg) {
                alert(err.msg);
            }
        }
    });

    const updownHandler = (v: boolean) => {
        v ? (setNum(num+1)):(setNum(num-1));
    }
    const buyHandler = () => {
        if (num > 0) {
            buyNumberMutation.mutate(num); 
        } else {
            alert("수량을 입력해주세요");
        }
    }
    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNum(parseInt(e.target.value));
    }
    const onClickHandler = () => {
        setData(true);
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Buy Number</h1>
        {isEmpty ? (
            <div id='buycontent'>
                <p>1회 발급당 200원이 차감됩니다</p>
                <input value={num} ref={numRef} onChange={onChangeHandler} style={InputStyle} placeholder='0'/> 
                <button style={buttonStyle} onClick={()=>updownHandler(true)}>+</button>
                <button style={buttonStyle} onClick={()=>updownHandler(false)}>-</button>    
                <button onClick={buyHandler} style={{ width: "50px", height: "30px", marginLeft: "20px",  }}>구매</button>
            </div> 
        ):( 
            <div>
                <ResultContainer numSentenceList={value} />
                <button onClick={onClickHandler} style={{ marginTop: "1cm", marginRight: "auto"}}>계속 구매하기</button>
            </div>
        )}
    </div>
  )
}

export default BuyNumber