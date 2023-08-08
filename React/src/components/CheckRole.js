import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { getAllCookie } from '../shared/Cookie';
import { checkLoginAndgetUserIf } from '../api/noneUserApi';
import { useMutation } from 'react-query';
import { setUserIf } from '../modules/userIfSlice';

export function checkLogin() {
    const dispatch = useDispatch();
    const userIf = useSelector(state=>state.userIf);

    const checkLoginMutation = useMutation(checkLoginAndgetUserIf, {
        onSuccess: (res)=>{
            console.log(res);
            dispatch(setUserIf(res))
        },
        onError: (err)=>{
            alert(err.message);
        }
    })

    useEffect(()=>{
        const tokens = getAllCookie().split(",");
        if (userIf === null && tokens.length === 2) {
            checkLoginMutation.mutate(tokens);
        }
    }, [userIf])

    return null;
}

export function AllowAll() {
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

export function AllowNotRoleUser() {
    const navigate = useNavigate();
    const role = useSelector(state=>state.userIf.role);

    useEffect(()=>{
        if  (role === "ROLE_USER") {
            alert("프리미엄 등록 이후 이용해주시기 바랍니다")
            navigate("/premium")
        } else if (role ==="") {
            alert("로그인 이후 이용해주세요")
            navigate("/signin")
        } 
    }, [role])

    return null;
}

export function AllowOnlyAdmin() {
    const navigate = useNavigate();
    const role = useSelector(state=>state.userIf.role);

    useEffect(()=>{
        if  (role !== "ROLE_ADMIN") {
            alert("접근 권한이 없습니다")
            navigate("/")
        }
    }, [role])

    return null;
}