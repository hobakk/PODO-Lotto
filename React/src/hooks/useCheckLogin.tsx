import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query';
import { useDispatch, useSelector } from 'react-redux';
import { checkLoginAndgetUserIf } from '../api/noneUserApi';
import { setUserIf } from '../modules/userIfSlice';
import { deleteToken, getAccessTAndRefreshT } from '../shared/Cookie';
import { RootState } from '../config/configStore';

function useCheckLogin() {
    type errorType = {
        status: number;
        message: string;
    }

    const dispatch = useDispatch();
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [isLogin, setData] = useState<boolean>();
    const [accessToken, refreshToken] = getAccessTAndRefreshT();

    const checkLoginMutation = useMutation(checkLoginAndgetUserIf, {
        onSuccess: (res)=>{
            if  (res.code === 200) {
                dispatch(setUserIf(res.data));
                setData(true)
            }
        },
        onError: (err: errorType)=>{
            if  (err.message === "SignatureException") {
                deleteToken();
            }
        }
    })

    useEffect(()=>{
        console.log(userIf)
        const { email, nickname, role } = userIf;
        if (!email && !nickname && !role && accessToken !== null && refreshToken !== null) {
            checkLoginMutation.mutate([ accessToken, refreshToken ]);
        } else if (email && nickname && role && accessToken !== null && refreshToken !== null) {
            setData(true);
        } else {
            setData(false);
        }
    }, [userIf])
  
    return isLogin;
}

export default useCheckLogin