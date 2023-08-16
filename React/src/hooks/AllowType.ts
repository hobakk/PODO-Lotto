import React, { useEffect, useState } from 'react'
import { useSelector } from 'react-redux/es/hooks/useSelector';
import { useNavigate } from 'react-router-dom';
import { RootState } from '../config/configStore';

export function useAllowType(type: string): boolean {
    const [isAllow, setIsAllow] = useState<boolean>(false);
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

        setIsAllow(true);
    }, [role])

    return isAllow;
}