import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import useUserInfo from '../../hooks/useUserInfo';

function Oauth2Redirect() {
  const navigate = useNavigate();
  const { getIfMutation } = useUserInfo();

  useEffect(()=>{ 
    getIfMutation.mutate(); 
    navigate("/");
  }, [])

  return null;
}

export default Oauth2Redirect