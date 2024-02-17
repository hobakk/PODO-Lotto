import React, { useEffect, useRef, useState } from 'react'
import { CommonStyle, InputBox, MsgAndInput, TitleStyle } from '../../shared/Styles'
import { buyNumber } from '../../api/sixNumberApi';
import { useMutation } from 'react-query';
import useUserInfo from '../../hooks/useUserInfo';
import { ResultContainer } from '../../components/Manufacturing';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';

function BuyNumber() {
    const [num, setNum] = useState<number>(0);
    const [value, setValue] = useState<string[]>([]);
    const [isEmpty, setData] = useState<boolean>(true);
    const numRef = useRef<HTMLInputElement>(null);
    const { getCashAndNickname } = useUserInfo();

    const buttonStyle: React.CSSProperties = {
        width: "30px",
        height: "30px",
    }
    
    useEffect(()=>{ 
        if (numRef.current) numRef.current.focus();
    }, [])

    const buyNumberMutation = useMutation<UnifiedResponse<string[]>, unknown, number>(buyNumber, {
        onSuccess: (res)=>{
            if  (res.code === 200 && res.data) {
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

    const updownHandler = (v: boolean) => {
        v ? (setNum(num+1)):(setNum(num-1));
    }

    const buyHandler = () => {
        if (num > 0 && num < 25) buyNumberMutation.mutate(num); 
        else if (num > 24) {
            alert("최대 24번까지 발급 가능합니다");
            setNum(24);
        } else alert("수량을 입력해주세요");
    }

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { value }: any = e.target;
        const count: number = parseInt(value);

        if (!isNaN(count)) setNum(count);
        else {
            alert("숫자만 입력 가능합니다");
            setNum(0);
        }
    }
    
    const onClickHandler = () => setData(true);

  return (
    <div style={ CommonStyle }>
        <h1 style={ TitleStyle }>랜덤 번호 발급</h1>
        <p style={{ marginBottom:"60px" }}>1회 발급당 100원이 차감됩니다</p>
        {isEmpty ? (
            <div style={{ display:"flex", fontSize:"25px", width:"17cm", placeItems:"center"}}>
                <span>발급 횟수:</span>
                <InputBox 
                    type="number"
                    value={num} 
                    ref={numRef} 
                    onChange={onChangeHandler} 
                    placeholder='0'
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