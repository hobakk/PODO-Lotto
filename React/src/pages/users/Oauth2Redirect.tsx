import React, { useEffect } from 'react'
import GetUserIfMutation from '../../components/GetUserIfMutation'
import { useNavigate } from 'react-router-dom';

function Oauth2Redirect() {
    const getUserIfMutation = GetUserIfMutation();
    const navigate = useNavigate();

    useEffect(()=>{ 
        getUserIfMutation.mutate(); 
        navigate("/");
    }, [])

  return null;
}

export default Oauth2Redirect