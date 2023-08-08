import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom';
import { getAllCookie } from '../shared/Cookie';
import { checkLoginAndgetUserIf } from '../api/noneUserApi';
import { useMutation } from 'react-query';
import { setUserIf } from '../modules/userIfSlice';

const withCheckLogin = (AllowType) => () => {
    const dispatch = useDispatch();
    const userIf = useSelector(state=>state.userIf);
    const { email, nickname, role } = userIf;
    const navigate = useNavigate();

    const checkLoginMutation = useMutation(checkLoginAndgetUserIf, {
        onSuccess: (res)=>{
            if  (res.code === 200) {
                console.log(res.data);
                dispatch(setUserIf(res.data));
            }
        },
        onError: (err)=>{
            alert(err.message);
        }
    })

    useEffect(() => {
        const tokens = getAllCookie().split(",");

        if ( !email && !nickname && !role && tokens.length === 2) {
            checkLoginMutation.mutate(tokens);
        } else {
            if (AllowType === "AllowAll") {
                if (userIf.role === "") {
                    alert("로그인 이후 이용해주세요");
                    navigate("/signin");
                }
            } else if (AllowType === "AllowNotRoleUser") {
                if (userIf.role === "ROLE_USER") {
                    alert("프리미엄 등록 이후 이용해주시기 바랍니다");
                    navigate("/premium");
                } else if (userIf.role === "") {
                    alert("로그인 이후 이용해주세요");
                    navigate("/signin");
                }
            } else if (AllowType === "AllowOnlyAdmin") {
                if (userIf.role !== "ROLE_ADMIN") {
                    alert("접근 권한이 없습니다");
                    navigate("/");
                }
            }
        }
      }, [userIf]);

    return null;
}

export const AllowAll = withCheckLogin("AllowAll");
export const AllowNotRoleUser = withCheckLogin("AllowNotRoleUser");
export const AllowOnlyAdmin = withCheckLogin("AllowOnlyAdmin");