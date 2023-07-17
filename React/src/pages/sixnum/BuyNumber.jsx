import React, { useEffect, useRef, useState } from 'react'
import { SignBorder, CommonStyle } from '../../components/Styles'
import { buyNumber } from '../../api/useUserApi';
import { useMutation } from 'react-query';


function BuyNumber() {
    const [num, setNum] = useState(0);
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
        const resultElement = document.getElementById("resultcontent");

        // if  (value === "") {
        //     buyElement.style.display = "block";
        //     resultElement.style.display = "none";
        // } else {
        //     buyElement.style.display = "none";
        //     resultElement.style.display = "block";
        // }
    }, [value])

    const onClickHandler = (v) => {
        v ? (setNum(num+1)):(setNum(num-1));
    }
    const buyHandler = () => {
        if (!num <= 0) {
            buyNumberMutation.mutate(num); 
        }
    }
    const onChangeHandler = (e) => {
        setNum(e.target.value);
    }

  return (
    <div style={ SignBorder }>
        <div style={ CommonStyle }>
            <h1 style={{  fontSize: "80px" }}>Buy Number</h1>
            <div id='buycontent'>
                <p>1회 발급당 200원이 차감됩니다</p>
                <input value={num} ref={numRef} onChange={onChangeHandler} style={InputStyle} placeholder='0'/> 
                <button style={buttonStyle} onClick={()=>onClickHandler(true)}>+</button>
                <button style={buttonStyle} onClick={()=>onClickHandler(false)}>-</button>    
                <button onClick={buyHandler} style={{ width: "50px", height: "30px", marginLeft: "20px",  }}>구매</button>
            </div> 
            {value !== "" ? (
                <div id='resultcontent' key={value} style={{ display: "none"}}>
                    {value}
                </div>
            ) : null}
            
        </div>
    </div>
  )
}

export default BuyNumber