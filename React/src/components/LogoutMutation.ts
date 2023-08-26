import React from 'react'
import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { logout } from '../api/userApi';
import { logoutUser } from '../modules/userIfSlice';
import { UnifiedResponse } from '../shared/TypeMenu';

function LogoutMutation() {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const logoutMutation = useMutation<UnifiedResponse<undefined>>(logout, {
        onSuccess: (res) =>{
            if (res.code === 200) {
                dispatch(logoutUser());
                navigate("/");
            }
        }
    })

  return logoutMutation;
}

export default LogoutMutation