import React from 'react'
import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux';
import { getInformation } from '../api/useUserApi';
import { setUserIf } from '../modules/userIfSlice';
import { UserIfState } from '../shared/TypeMenu';

function GetUserIfMutation() {
    const dispatch = useDispatch();

    const getIfMutation = useMutation(getInformation, {
        onSuccess: (res: UserIfState)=>{
            dispatch(setUserIf(res));
        }
    })

  return getIfMutation;
}

export default GetUserIfMutation