import { useMutation } from 'react-query';
import { UnifiedResponse } from '../shared/TypeMenu';
import { attendance } from '../api/userApi';

function useAttendance() {

    const attendanceMutation = useMutation<UnifiedResponse<undefined>>(attendance, {
        onSuccess: (res) => {
            alert(res.msg);
        },
        onError: (err)=>{
            alert(err)
        }
    })

  return attendanceMutation;
}

export default useAttendance;