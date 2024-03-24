import { useMutation } from 'react-query';
import { UnifiedResponse } from '../shared/TypeMenu';
// import { deleteCookie } from '../api/noneUserApi';
import { useNavigate } from 'react-router-dom';
import { deleteCookie } from '../api/userApi';

function useDeleteCookie() {
    const navigate = useNavigate();

    const mutation = useMutation<UnifiedResponse<undefined>>(deleteCookie, {
        onSuccess: (res) => {
            navigate("/");
        }
    })

  return mutation;
}

export default useDeleteCookie;