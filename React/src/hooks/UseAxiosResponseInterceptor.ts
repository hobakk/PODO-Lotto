import { useEffect } from "react"
import { api } from "../api/config"
import { useSelector } from "react-redux";
import { RootState } from "../config/configStore";
import { useNavigate } from "react-router-dom";
import { UserDetailInfo } from "../shared/TypeMenu";

const UesAxiosResponseInterceptor = () => {
    const DONT_LOGIN = "DONT_LOGIN";
    const ACCESS_DENIED = "ACCESS_DENIED";
    const ONLY_ADMIN_ACCESS_API = "ONLY_ADMIN_ACCESS_API";
    const reduxState = useSelector((state: RootState)=>state);
    const navigate = useNavigate();

    useEffect(()=>{
        const interceptor = api.interceptors.response.use(
            response => response,
            async error => {
                const { config, response } = error;
                const result = response.data;
                const exceptionType = result.exceptionType;
                const userIf: UserDetailInfo = reduxState.userIf;
                const refreshToken: string = reduxState.refreshToken.refreshToken;
                
                if (exceptionType === DONT_LOGIN) {
                    alert(result.msg);
                    navigate("/signin");
                    return Promise.resolve();
                } else if (exceptionType === ACCESS_DENIED) {
                    alert(result.msg);
                    navigate("/premium");
                    return Promise.resolve();
                } else if (exceptionType === ONLY_ADMIN_ACCESS_API) {
                    alert(result.msg);
                    navigate("/");
                    return Promise.resolve();
                } else if (exceptionType === "RE_ISSUANCE") {
                    await api.post(`/jwt/re-issuance`, {
                        userId: userIf.userId,
                        email: userIf.email,
                        refreshToken: refreshToken,
                    })
                    .then((res) => {
                        if (res.data.code === 200) {
                            console.log(config);
                            return api(config);
                        }
                    })
                    .catch((err) => {
                        console.log("userIf", userIf);
                        console.error("재발급 실패", err);
                    })
                }
        
                return Promise.reject(result);
            }
        );

        return () => { api.interceptors.response.eject(interceptor); }
    }, []);
}

export default UesAxiosResponseInterceptor;