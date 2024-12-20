import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query';
import { createLotto } from '../../api/lottoApi';
import { ButtonStyle, CommonStyle, TitleStyle } from '../../shared/Styles';
import { useNavigate } from 'react-router-dom';
import { UnifiedResponse, Err } from '../../shared/TypeMenu';
import { checkMain } from '../../api/lottoApi';

function CreateMainLotto() {
    const navigate = useNavigate();
    const [count, setCount] = useState<number>();
    const [isOk, setCheckMain] = useState<Boolean>(false);

    const chekcMainMutation = useMutation<Boolean>(checkMain, {
        onSuccess: (res) => {
            setCheckMain(res);
        }
    })

    const setMainLottoMutation = useMutation<UnifiedResponse<undefined>>(createLotto, {
        onSuccess: (res) => {
            alert(res.msg);
            navigate("/");
        },
        onError: (err: any | Err)=>{
            if (err.status) {
                alert(err.message);
                navigate("/");
            } else {
                alert(err.msg);
                navigate("/");
            }
        }
    })

    const onClickHandler = () => {
        if (isOk && count === 0) {
            setCount(count + 1);
            alert("정말 초기화 하시겠습니까 ? 재클릭 필요");
        } else setMainLottoMutation.mutate();
    }

    useEffect(()=> {
        chekcMainMutation.mutate();
        setCount(0);
    }, [])

  return (
    <div id='recent' style={ CommonStyle }>
        {isOk ? (
            <h1 style={ TitleStyle }>로또 메인 객체 초기화</h1>
        ):(
            <h1 style={ TitleStyle }>로또 메인 생성</h1>
        )}
        <button style={ButtonStyle} onClick={onClickHandler}>{isOk ? ("초기화") : ("생성")}</button>
    </div>
  )
}

export default CreateMainLotto