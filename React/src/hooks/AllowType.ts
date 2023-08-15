import React, { useEffect } from 'react'
import { useSelector } from 'react-redux/es/hooks/useSelector';
import { useNavigate } from 'react-router-dom';
import { RootState } from '../config/configStore';

function AllowType(type: string) {
    const role = useSelector((state: RootState)=>state.userIf.role) as string;
    const navigate = useNavigate();

    useEffect(()=>{
        if (type === "AllowLogin") {
            if (role === "") {
                alert("로그인 이후 이용해주세요");
                navigate("/signin");
            }
        } else if (type === "AllowNotRoleUser") {
            if (role === "ROLE_USER") {
                alert("프리미엄 등록 이후 이용해주시기 바랍니다");
                navigate("/premium");
            } else if (role === "") {
                alert("로그인 이후 이용해주세요");
                navigate("/signin");
            }
        } else if (type === "AllowOnlyAdmin") {
            if (role !== "ROLE_ADMIN") {
                alert("접근 권한이 없습니다");
                navigate("/");
            }
        }
    }, [role])

    return null;
}

export const AllowLogin = AllowType("AllowLogin");
export const AllowNotRoleUser = AllowType("AllowNotRoleUser");
export const AllowOnlyAdmin = AllowType("AllowOnlyAdmin");