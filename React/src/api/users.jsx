import axios from "axios";
import { getCookie } from "../shared/Cookie";

const url = `${process.env.REACT_APP_SPRING_URL}/users`;

const signup = async (v) => {
    await axios.post(`${url}/signup`, v);
}

const signin = async (v) => {
    await axios.post(`${url}/signin`, v, {withCredentials: true}
    );
}

const logout = async () => {
    await axios.post(`${url}/logout`, {withCredentials: true})
}

const getAccessToken = async () => {
    const accessToken = getCookie("accessToken");
    if (accessToken !== null) {
        return accessToken;
    } else {
        alert("로그인을 해주세요")
        window.location.href = "/signin";
    }
}

const getCash = async () => {
    const cashNickname = await axios({
        method: `get`,
        url: `${url}/cash`,
    })

    return cashNickname.data.data;
}

export { signup, signin, getCash, getAccessToken, logout };