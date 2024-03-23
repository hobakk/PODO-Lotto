import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, MsgAndInput, InputBox, TitleStyle } from '../../shared/Styles'
import { checkIdNextIssuanceAccess, withdraw } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { RootState, persistor } from '../../config/configStore';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';

function CheckIdNextIssuanceAccess() {
    const userIf = useSelector((state: RootState)=>state.userIf);
    const navigate = useNavigate();
    const purge = async () => { await persistor.purge(); }

    const mutation = useMutation<UnifiedResponse<undefined>, Err, string>(checkIdNextIssuanceAccess, {
        onSuccess: (res)=>{
            if (res.code === 200) navigate("/");
        },
        onError: (err) =>{
            alert("다시 로그인 해주세요");
            purge();
            // 쿠키 삭제하는 api 추가 필
        }
    })

    useEffect(()=>{
        if (userIf.userId) mutation.mutate(String(userIf.userId));
    }, [userIf])

    return (
        <div>
            <h1 style={TitleStyle}>로그인 정보 갱신 페이지 </h1>
        </div>
    )
}

export default CheckIdNextIssuanceAccess