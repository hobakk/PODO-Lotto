import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { buyNumber } from '../../api/sixNumberApi';
import { useMutation } from 'react-query';
import { ResultContainer } from '../../components/Manufacturing';
import { UnifiedResponse } from '../../shared/TypeMenu';

function StatisticalNumber() {
    const [num, setNum] = useState<number>(0);
    const [repetition, setRepetition] = useState<number>(0);
    const [value, setValue] = useState<string[]>([]);
    const numRef = useRef<HTMLInputElement>(null);

    useEffect(()=>{
        if (numRef.current) 
            numRef.current.focus();
    }, [])

    const InputStyle: React.CSSProperties = {
        width: "5cm",
        height: "25px",
    }
    const buttonStyle: React.CSSProperties = {
        width: "30px",
        height: "30px",
    }
    
    const buyNumberMutation = useMutation<UnifiedResponse<string[]>, void, number>(buyNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            setValue(res.data);
        }
    });

    const onClickHandler = (v: boolean) => {
        v ? (setNum(num+1)):(setNum(num-1));
    }
    const buyHandler = () => {
        if (num > 0 && repetition > 0) {
            buyNumberMutation.mutate(num); 
        } else {
            alert("반복횟수 및 발급횟수를 입력해주세요");
        }
    }
    const setNumOnChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setNum(parseInt(value));
    }
    const serRepetitionOnChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setRepetition(parseInt(value));
    }

  return (
    <div style={ CommonStyle }>
        <h1 style={{  fontSize: "80px" }}>Statistical Number</h1>
        {value.length === 0 ? (
            <div id='buycontent'>
                <p>반복횟수 입력란 </p>
                <input value={repetition} onChange={serRepetitionOnChangeHandler} ref={numRef} style={InputStyle} placeholder='10만 이하 수를 입력해주세요'/>
                <p style={{ marginTop: "40px" }}>1회 발급당 300원이 차감됩니다</p>
                <input value={num} onChange={setNumOnChangeHandler} style={InputStyle} placeholder='0' /> 
                <button style={buttonStyle} onClick={()=>onClickHandler(true)}>+</button>
                <button style={buttonStyle} onClick={()=>onClickHandler(false)}>-</button>    
                <button onClick={buyHandler} style={{ width: "50px", height: "30px", marginLeft: "20px",  }}>구매</button>
            </div> 
        ):(
            <div>
                <ResultContainer numSentenceList={value}></ResultContainer>
                <button onClick={()=>{setValue([])}} style={{ marginTop: "1cm", marginRight: "auto"}}>계속 구매하기</button>
            </div>                
        )}
    </div>
  )
}

export default StatisticalNumber