import { api } from "./config";

const getInformation = async () => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

const logout = async () => {
    await api.post(`/users/logout`)
}

const withdraw = async (msg) => {
    console.log(msg)
    await api.patch("/users/withdraw", msg);
}

const checkPW = async (msg) => {
    const { data } = await api.post("/users/check-pw", msg);
    return data.code;
}

const update = async (inputValue) => {
    const { data } = await api.patch("/users/update", inputValue);
    return data.code;
}

export { getInformation, logout, withdraw, checkPW, update };