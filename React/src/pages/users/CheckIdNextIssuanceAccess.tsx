import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { CommonStyle, MsgAndInput, InputBox, TitleStyle } from '../../shared/Styles'
import { checkIdNextIssuanceAccess, withdraw } from '../../api/userApi';
import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { RootState, persistor } from '../../config/configStore';
import { Err, UnifiedResponse } from '../../shared/TypeMenu';
import useDeleteCookie from '../../hooks/useDeleteCookie';

function CheckIdNextIssuanceAccess() {
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [id, setid] = useState<number>(-1);
    const navigate = useNavigate();
    const purge = async () => { await persistor.purge(); }
    const deleteCookieMutation = useDeleteCookie();

    const mutation = useMutation<UnifiedResponse<undefined>, Err, number>(checkIdNextIssuanceAccess, {
        onSuccess: (res)=>{
            if (res.code === 200) navigate("/");
        },
        onError: (err) =>{
            alert("다시 로그인 해주세요");
            purge();
            deleteCookieMutation.mutate();
        }
    })

    useEffect(()=>{
        setid(userIf.userId);
    }, [userIf])

    useEffect(()=>{
        console.log(id)
        if (id === 0) {
            purge();
            deleteCookieMutation.mutate();  
        } else if (id > 0) mutation.mutate(id);
    }, [id])

    return (
        <div>
            <h1 style={TitleStyle}>로그인 정보 갱신 페이지 </h1>
        </div>
    )
}

export default CheckIdNextIssuanceAccess