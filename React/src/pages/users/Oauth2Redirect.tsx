import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import { useMutation } from 'react-query';
import { getUserIfAndRefreshToken } from '../../api/userApi';
import { useDispatch } from 'react-redux';
import { setUserIf } from '../../modules/userIfSlice';
import { Err } from '../../shared/TypeMenu';

function Oauth2Redirect() {
  const dispatch = useDispatch();  
  const navigate = useNavigate();

  const getUserIfAndRefreshMutation = useMutation(getUserIfAndRefreshToken, {
    onSuccess: (res)=>{
      if (res.code === 200 && res.data) {
          dispatch(setUserIf(res.data));
      }
    },
    onError: (err: Err)=>{
      alert(err.msg);
    }
  });

  useEffect(()=>{ 
    getUserIfAndRefreshMutation.mutate(); 
    navigate("/");
  }, [])

  return null;
}

export default Oauth2Redirect