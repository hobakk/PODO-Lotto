import { useMutation } from 'react-query';
import { useDispatch } from 'react-redux';
import { CashNicknameDto, getCashNickname, getInformation } from '../api/userApi';
import { setCashNickname, setUserIf } from '../modules/userIfSlice';
import { UserDetailInfo } from '../shared/TypeMenu';
import { UnifiedResponse } from '../shared/TypeMenu';

function useUserInfo() {
    const dispatch = useDispatch();

    const getIfMutation = useMutation<UnifiedResponse<UserDetailInfo>>(getInformation, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) {
                dispatch(setUserIf(res.data));
            }
        }
    });

    const getCashAndNickname = useMutation<UnifiedResponse<CashNicknameDto>>(getCashNickname, {
        onSuccess: (res)=>{
            if (res.code === 200 && res.data) dispatch(setCashNickname(res.data));
        }
    });

    return { getIfMutation, getCashAndNickname };
}

export default useUserInfo;