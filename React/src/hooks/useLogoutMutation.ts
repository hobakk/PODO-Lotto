import { useMutation } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { logout } from '../api/userApi';
import { UnifiedResponse } from '../shared/TypeMenu';
import { persistor } from '../config/configStore';
import useDeleteCookie from './useDeleteCookie';

function useLogoutMutation() {
    const navigate = useNavigate();
    const purge = async () => { await persistor.purge(); }
    const deleteCookieMutation = useDeleteCookie();

    const logoutMutation = useMutation<UnifiedResponse<undefined>>(logout, {
        onSuccess: (res) => {
            if (res.code === 200) {
                purge();
                deleteCookieMutation.mutate();
            }
        }
    })

  return logoutMutation;
}

export default useLogoutMutation;