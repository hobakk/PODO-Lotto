import React, { useEffect, useState } from 'react'
import { CommonStyle } from '../../components/Styles'
import { useMutation } from 'react-query';
import { getSearch } from '../../api/useUserApi';

function SearchCharges() {
    const [inputValue, setInputValue] = useState([]);
    const [value, setValue] = useState("");

    const getSearchMutation = useMutation(getSearch, {
        onSuccess: (res)=>{
            if (res.code === 200) {
                console.log("들어옴")
                setValue(res.data);
            }
        },
        onError: (err) => {
            if (err.status === 500) {
                alert(err.message);
            }
            setValue([]);
        }
    })

    const onChangeHandler = (e) => {
        const { name, value } = e.target;
        setInputValue({
            ...inputValue,
            [name] : value,
        })
    }

    const onSubmitHandler = (e) => {
        e.preventDefault();
        if (inputValue.msg === "") {
            alert("msg 를 입력하세요");
        } else {
            const msg = inputValue.msg;
            let cash = inputValue.cash;
            getSearchMutation.mutate({ msg, cash });
        }
    }

    useEffect(()=>{console.log(value)}, [value])

    const MapBorderStyle = {
        display: "flex",
        flexDirection: "column",
        border: "3px solid black",
        width: "12cm",
        marginBottom: "5px"
      }

    const InputStyle = {
        width: "7cm",
        height: "0.8cm",
    }

  return (
    <div id='recent' style={{ ...CommonStyle, fontSize:"20px"}}>
        <h1 style={{  fontSize: "80px" }}>Search Charges</h1>
        {value === "" ? (
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