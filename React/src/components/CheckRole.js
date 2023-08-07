import React, { useEffect } from 'react'
import { useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';

function CheckRole() {
    const navigate = useNavigate();
    const role = useSelector(state=>state.userIf.role);

    useEffect(()=>{
        if  (role ==="") {
            alert("로그인 이후 이용해주세요")
            navigate("/signin")
        } 
    }, [role])

    return null;
}

export default CheckRole