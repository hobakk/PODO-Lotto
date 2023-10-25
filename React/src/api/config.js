import axios from "axios";

const url = `${process.env.REACT_APP_DOMAIN}`

const dontLogin = axios.create({
    headers: {
        "Content-Type": 'application/json',	// 서버 domain, httpOnly 설정 때문에 추가
    },
    baseURL: `${url}/api`,
    withCredentials: true,
})

const api = axios.create({
    baseURL: `${url}/api`,
    withCredentials: true,
})

export { dontLogin, api };