import React from 'react'
import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux';
import { getInformation } from '../api/userApi';
import { setUserIf } from '../modules/userIfSlice';
import { UserIfState } from '../shared/TypeMenu';
import { UnifiedResponse } from '../shared/TypeMenu';

function GetUserIfMutation() {
    const dispatch = useDispatch();

    const getIfMutation = useMutation<UnifiedResponse<UserIfState>>(getInformation, {
        onSuccess: (res)=>{
            console.log(res)
            if (res.code === 200 && res.data)
            dispatch(setUserIf(res.data));
        }
    })

  return getIfMutation;
}

export default GetUserIfMutation