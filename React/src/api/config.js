import axios from "axios";

const url = `${process.env.REACT_APP_SPRING_URL}`

const signApi = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080'	// 서버 domain, httpOnly 설정 때문에 추가
    },
    baseURL: `${url}/users`,
    withCredentials: true,
})

const api = axios.create({
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080',
    },
    baseURL: url,
    withCredentials: true,
})

export { signApi, api };