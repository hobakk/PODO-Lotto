import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, TitleStyle } from '../../shared/Styles'
import { statisticalNumber, repetitionAndNum } from '../../api/sixNumberApi';
import { useMutation } from 'react-query';
import { ResultContainer } from '../../components/Manufacturing';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import useUserInfo from '../../hooks/useUserInfo';

function StatisticalNumber() {
    const [inputNum, setInputNum] = useState<repetitionAndNum>({
        value: 0,
        repetition: 0,
    });
    const [value, setValue] = useState<string[]>([]);
    const [isEmpty, setData] = useState<boolean>(true);
    const numRef = useRef<HTMLInputElement>(null);
    const { getCashAndNickname } = useUserInfo();

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
    
    const buyNumberMutation = useMutation<UnifiedResponse<string[]>, unknown, repetitionAndNum>(statisticalNumber, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                setValue(res.data);
                setData(false);
                getCashAndNickname.mutate();
            }
        },
        onError: (err: any | Err)=>{
            if (err.status) alert(err.message);
            else if (err.msg) alert(err.msg);
        }
    });

    const onClickHandler = (v: string) => {
        if (v === "+") {
            setInputNum({
                ...inputNum,
                ["value"]: inputNum["value"] + 1,
            })
        } else {
            setInputNum({
                ...inputNum,
                ["repetition"]: inputNum["repetition"] + 1,
            })
        }
    }

    const finalBuyHandler = () => {
        if (inputNum["value"] > 0 && inputNum["repetition"] > 0) {
            buyNumberMutation.mutate(inputNum); 
        } else {
            alert("반복횟수 및 발급횟수를 입력해주세요");
        }
    }

    const validateValue = (value: any) => {
        const count: number = parseInt(value);

        if (!isNaN(count)) return count;
        else alert("숫자만 입력 가능합니다");
    }

    const setNumOnChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        const count = validateValue(value);

        if (count && count !== 0) setInputNum({
            ...inputNum,
            [name]: count,
        });
    }

    const isEmptyHandler = () => setData(true);

  return (
    <div style={ CommonStyle }>
        <h1 style={ TitleStyle }>Statistical Number</h1>
        {isEmpty ? (
            <div id='buycontent'>
                <p>반복횟수 입력란 </p>
                <input 
                    name="repetition"
                    value={inputNum["repetition"]} 
                    onChange={setNumOnChangeHandler} 
                    ref={numRef} 
                    style={InputStyle} 
                    placeholder='10만 이하 수를 입력해주세요'
                />
                <p style={{ marginTop: "40px" }}>1회 발급당 200원이 차감됩니다</p>
                <input 
                    name="value"
                    value={inputNum["value"]} 
                    onChange={setNumOnChangeHandler} 
                    style={InputStyle} 
                    placeholder='0' 
                /> 
                <button style={buttonStyle} onClick={()=>onClickHandler("+")}>+</button>
                <button style={buttonStyle} onClick={()=>onClickHandler("-")}>-</button>    
                <button onClick={finalBuyHandler} style={{ width: "50px", height: "30px", marginLeft: "20px",  }}>구매</button>
            </div> 
        ):(
            <div>
                <ResultContainer numSentenceList={value}></ResultContainer>
                <button onClick={isEmptyHandler} style={{ marginTop: "1cm", marginRight: "auto"}}>계속 구매하기</button>
            </div>                
        )}
    </div>
  )
}

export default StatisticalNumber