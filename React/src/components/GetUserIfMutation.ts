import React from 'react'
import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux';
import { getInformation } from '../api/userApi';
import { setUserIf } from '../modules/userIfSlice';
import { UserDetailInfo } from '../shared/TypeMenu';
import { UnifiedResponse } from '../shared/TypeMenu';

function GetUserIfMutation() {
    const dispatch = useDispatch();

    const getIfMutation = useMutation<UnifiedResponse<UserDetailInfo>>(getInformation, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data)
            dispatch(setUserIf(res.data));
        }
    })

  return getIfMutation;
}

export default GetUserIfMutation