import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { buyNumber } from '../../api/useUserApi';
import { useMutation } from 'react-query';
import { ResultContainer } from '../../components/Manufacturing';
import { AllowLogin } from '../../components/CheckRole';

function StatisticalNumber() {
    const [num, setNum] = useState(0);
    const [repetition, setRepetition] = useState(0);
    const [value, setValue] = useState("");
    const numRef = useRef();

    useEffect(()=>{
        numRef.current.focus();
    }, [])

    const InputStyle = {
        width: "5cm",
        height: "25px",
    }
    const buttonStyle = {
        width: "30px",
        height: "30px",
    }
    
    const buyNumberMutation = useMutation(buyNumber, {
        onSuccess: (res)=>{
            setValue(res);
        }
    });

    useEffect(()=>{
        const buyElement = document.getElementById("buycontent");

        if  (value === "") {
            buyElement.style.display = "block";
        } else {
            buyElement.style.display = "none";
        }
    }, [value])

    const onClickHandler = (v) => {
        v ? (setNum(num+1)):(setNum(num-1));
    }
    const buyHandler = () => {
        if (!num <= 0 && !repetition <= 0) {
            buyNumberMutation.mutate(num); 
        } else {
            alert("반복횟수 및 발급횟수를 입력해주세요");
        }
    }
    const onChangeHandler = (e) => {
        setNum(e.target.value);
    }

  return (
    <div style={ CommonStyle }>
        <AllowLogin />
        <h1 style={{  fontSize: "80px" }}>Statistical Number</h1>
        <div id='buycontent'>
            <p>반복횟수 입력란 </p>
            <input value={repetition} onChange={(e)=>{setRepetition(e.target.value)}} ref={numRef} style={InputStyle} placeholder='10만 이하 수를 입력해주세요'/>
            <p style={{ marginTop: "40px" }}>1회 발급당 300원이 차감됩니다</p>
            <input value={num} onChange={onChangeHandler} style={InputStyle} placeholder='0'/> 
            <button style={buttonStyle} onClick={()=>onClickHandler(true)}>+</button>
            <button style={buttonStyle} onClick={()=>onClickHandler(false)}>-</button>    
            <button onClick={buyHandler} style={{ width: "50px", height: "30px", marginLeft: "20px",  }}>구매</button>
        </div> 
        {value !== "" ? (
            <div>
                <ResultContainer numSentenceList={value}></ResultContainer>
                <button onClick={()=>{setValue("")}} style={{ marginTop: "1cm", marginRight: "auto"}}>계속 구매하기</button>
            </div>                
        ) : null}
    </div>
  )
}

export default StatisticalNumber