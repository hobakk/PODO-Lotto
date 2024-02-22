import { useMutation } from 'react-query';
import { UnifiedResponse } from '../shared/TypeMenu';
import { attendance } from '../api/userApi';
import useUserInfo from './useUserInfo';

function useAttendance() {
    const { getCashAndNickname } = useUserInfo();

    const attendanceMutation = useMutation<UnifiedResponse<undefined>>(attendance, {
        onSuccess: (res) => {
            getCashAndNickname.mutate();
            alert(res.msg);
        },
        onError: (err)=>{
            alert (err);
        }
    })

  return attendanceMutation;
}

export default useAttendance;