import React, { useEffect, useState } from 'react'
import { useMutation } from 'react-query';
import { useDispatch, useSelector } from 'react-redux';
import { checkLoginAndgetUserIf } from '../api/noneUserApi';
import { setUserIf } from '../modules/userIfSlice';
import { RootState } from '../config/configStore';
import { Err, UserIfState, UnifiedResponse } from '../shared/TypeMenu';

function useCheckLogin() {
    const dispatch = useDispatch();
    const userIf = useSelector((state: RootState)=>state.userIf);
    const [isLogin, setIsLogin] = useState<boolean>(false);

    const checkLoginMutation = useMutation<UnifiedResponse<UserIfState>, Err>(checkLoginAndgetUserIf, {
        onSuccess: (res)=>{
            if  (res.code === 200 && res.data) {
                console.log(res.data)
                dispatch(setUserIf(res.data));
                setIsLogin(true)
            }
        },
        onError: (err)=>{
            if (err.msg) {
                alert(err.msg);
            }

            setIsLogin(false);
        }
    })

    useEffect(()=>{
        const { email, nickname, role } = userIf;
        if (!email && !nickname && !role) {
            const timer = setTimeout(()=>{
                checkLoginMutation.mutate();
            }, 1000)
            return ()=> clearTimeout(timer);
        } else if (email && nickname && role) {
            setIsLogin(true);
        } else {
            setIsLogin(false);
        }
    }, [userIf])
  
    return isLogin;
}

export default useCheckLogin