import { api } from "./config";

const getInformation = async () => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

const logout = async () => {
    await api.post(`/users/logout`)
}

export { getInformation, logout };