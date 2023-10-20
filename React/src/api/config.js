import axios from "axios";

const url = `${process.env.REACT_APP_SPRING_URL}`

const dontLogin = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://172.31.41.246:8080',
        "Content-Type": 'application/json',	// 서버 domain, httpOnly 설정 때문에 추가
    },
    baseURL: url,
    withCredentials: true,
})

const api = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://172.31.41.246:8080',
    },
    baseURL: url,
    withCredentials: true,
})

export { dontLogin, api };