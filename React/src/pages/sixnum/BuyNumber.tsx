import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, InputBox, InputBoxStyle, MsgAndInput } from '../../components/Styles'
import { buyNumber } from '../../api/sixNumberApi';
import { useMutation } from 'react-query';
import GetUserIfMutation from '../../components/GetUserIfMutation';
import { ResultContainer } from '../../components/Manufacturing';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function BuyNumber() {
    const [num, setNum] = useState<number>(0);
    const [value, setValue] = useState<string[]>([]);
    const [isEmpty, setData] = useState<boolean>(true);
    const numRef = useRef<HTMLInputElement>(null);
    const getUserIfMutation = GetUserIfMutation();

    const buttonStyle: React.CSSProperties = {
        width: "30px",
        height: "30px",
    }
    
    useEffect(()=>{ 
        if (numRef.current)
            numRef.current.focus();
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
        const { value }: any = e.target;
        let count: number | string;
        if (typeof value === "number") count = value;
        else {
            alert("숫자만 입력 가능합니다");
            count = 0;
        }
        
        setNum(count);
    }
    const onClickHandler = () => {
        setData(true);
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px", textAlign:"center" }}>Buy Number</h1>
        <p style={{ marginBottom:"60px" }}>1회 발급당 200원이 차감됩니다</p>
        {isEmpty ? (
            <div style={{ display:"flex", fontSize:"25px", width:"17cm", placeItems:"center"}}>
                <span>발급 횟수:</span>
                <InputBox 
                    value={num} 
                    ref={numRef} 
                    onChange={onChangeHandler} 
                    placeholder='0'
                    style={InputBoxStyle} 
                /> 
                <button style={buttonStyle} onClick={()=>updownHandler(true)}>+</button>
                <button style={buttonStyle} onClick={()=>updownHandler(false)}>-</button>    
                <button onClick={buyHandler} style={{ width: "80px", height: "30px", marginLeft: "20px",  }}>구매</button>
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