import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { getSearch } from '../../api/useUserApi';
import { AdminGetCharges, Res, errorType } from '../../shared/TypeMenu';
import { AllowOnlyAdmin, useAllowType } from '../../hooks/AllowType';

function SearchCharges() {
    useAllowType(AllowOnlyAdmin);
    const [inputValue, setInputValue] = useState<{msg: string, cash: number}>({
        msg: "",
        cash: 0,
    });
    const [value, setValue] = useState<AdminGetCharges>({
        userId: 0,
        msg: "",
        value: 0,
    });

    const getSearchMutation = useMutation(getSearch, {
        onSuccess: (res: Res)=>{
            if (res.code === 200) {
                setValue(res.data);
            }
        },
        onError: (err: errorType) => {
            if (err.code === 500) {
                alert(err.message);
            }
            setValue({userId: 0, msg: "", value: 0});
        }
    })

    const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setInputValue({
            ...inputValue,
            [name] : value,
        })
    }

    const onSubmitHandler = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (inputValue.msg === "") {
            alert("msg 를 입력하세요");
        } else {
            const msg = inputValue.msg;
            let cash = inputValue.cash;
            getSearchMutation.mutate({ msg, cash });
        }
    }

    const MapBorderStyle: React.CSSProperties = {
        display: "flex",
        flexDirection: "column",
        border: "3px solid black",
        width: "12cm",
        marginBottom: "5px"
      }

    const InputStyle: React.CSSProperties = {
        width: "7cm",
        height: "0.8cm",
    }

  return (
    <div id='recent' style={{ ...CommonStyle, fontSize:"20px"}}>
        <AllowOnlyAdmin />
        <h1 style={{  fontSize: "80px" }}>Search Charges</h1>
        {value.userId === 0 ? (
            <form onSubmit={onSubmitHandler}>
                <div>
                    <input 
                        style={InputStyle} 
                        name='msg'
                        placeholder='msg 를 입력하세요'
                        onChange={onChangeHandler}
                    />
                </div>
                <div style={{ marginTop:"0.5cm", marginBottom:"1cm" }}>
                    <input 
                        style={InputStyle} 
                        name='cash' 
                        placeholder='cash 를 입력하세요'
                        onChange={onChangeHandler}
                    />
                </div>
                <button style={InputStyle}>조회하기</button>
            </form>
        ):(
            <div>
                <div style={MapBorderStyle}>
                    <div style={{ display:"flex", padding: "10px" }}>
                        <span>msg: {value.msg}</span>
                        <span style={{ marginLeft:"auto"}}>cash: {value.value}</span>
                    </div>
                </div>
            </div>
        )}
    </div>
  )
}

export default SearchCharges