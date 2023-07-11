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

export { getInformation, logout, withdraw };