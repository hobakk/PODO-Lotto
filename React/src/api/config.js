import axios from "axios";

const url = `${process.env.REACT_APP_BASE_URL}`

const dontLogin = axios.create({
    headers: {
        'Access-Control-Allow-Origin': `${url}:8080`,
        "Content-Type": 'application/json',	// 서버 domain, httpOnly 설정 때문에 추가
    },
    baseURL: `${url}/api`,
    withCredentials: true,
})

const api = axios.create({
    headers: {
        'Access-Control-Allow-Origin': `${url}:8080`,
    },
    baseURL: `${url}/api`,
    withCredentials: true,
})

export { dontLogin, api };