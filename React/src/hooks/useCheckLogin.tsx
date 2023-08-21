import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query';
import { useDispatch, useSelector } from 'react-redux';
import { checkLoginAndgetUserIf } from '../api/noneUserApi';
import { setUserIf } from '../modules/userIfSlice';
import { deleteToken, getAccessTAndRefreshT } from '../shared/Cookie';
import { RootState } from '../config/configStore';
import { Err, UserIfState, UnifiedResponse } from '../shared/TypeMenu';

function useCheckLogin() {
    const dispatch = useDispatch();
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [isLogin, setData] = useState<boolean>(false);
    const [accessToken, refreshToken] = getAccessTAndRefreshT();

    const checkLoginMutation = useMutation<UnifiedResponse<UserIfState>, Err, string[]>(checkLoginAndgetUserIf, {
        onSuccess: (res: UnifiedResponse<UserIfState>)=>{
            if  (res.code === 200 && res.data !== undefined) {
                dispatch(setUserIf(res.data));
                setData(true)
            }
        },
        onError: (err: Err)=>{
            if  (err.message === "SignatureException") {
                deleteToken();
            }
        }
    })

    useEffect(()=>{
        const { email, nickname, role } = userIf;
        if (!email && !nickname && !role && accessToken !== undefined && refreshToken !== undefined) {
            checkLoginMutation.mutate([ accessToken, refreshToken ]);
        } else if (email && nickname && role && accessToken !== undefined && refreshToken !== undefined) {
            setData(true);
        } else {
            setData(false);
        }
    }, [userIf])
  
    return isLogin;
}

export default useCheckLogin