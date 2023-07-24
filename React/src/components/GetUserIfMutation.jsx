import React from 'react'
import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { getInformation } from '../api/useUserApi';
import { setUserIf } from '../modules/userIfSlice';

function GetUserIfMutation() {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const getIfMutation = useMutation(getInformation, {
        onSuccess: (userIf)=>{
            dispatch(setUserIf(userIf));
        }
    })

  return getIfMutation;
}

export default GetUserIfMutation