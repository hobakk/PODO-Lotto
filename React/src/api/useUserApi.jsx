import { api } from "./config";

// User
const getInformation = async () => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

const logout = async () => {
    await api.post(`/users/logout`)
}

const withdraw = async (msg) => {
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

const setCharges = async (inputValue) => {
    const { data }= await api.post("/users/charging", inputValue);
    return data;
}

const getCharges = async () => {
    const { data } = await api.get("/users/charging");
    return data;
}

const setPaid = async (msg) => {
    const { data } = await api.post("/users/paid", msg);
    return data;
}

const getStatement = async () => {
    const { data } = await api.get("/users/statement");
    return data;
}

// Admin
const getUsers = async () => {
    const { data } = await api.get("/admin/users");
    return data.data;
}

const getAdminCharges = async () => {
    const { data } = await api.get("/admin/charges");
    return data.data;
}

const getSearch = async (inputValue) => {
    const { data } = await api.get("/admin/search", inputValue);
    return data.data;
}

const setAdmin = async (userId, msg) => {
    const { data } = await api.patch(`/admin/users/${userId}`, msg);
    return data.code;
}

const upCash = async (inputValue) => {
    const { data } = await api.patch("/admin/users/up-cash", inputValue);
    return data.code;
}

const downCash = async (inputValue) => {
    const { data } = await api.patch("/admin/users/down-cash", inputValue);
    return data.code;
}

const createLotto = async () => {
    const { data } = await api.post("/admin/lotto");
    return data.code;
}

const setStatus = async (userId, msg) => {
    const { data } = await api.patch(`/admin/status/${userId}`, msg);
    return data.code;
}

const setWinNumber = async (inputValue) => {
    const { data } = await api.post("/admin/winnumber", inputValue);
    return data.code;
}

export { 
    getInformation, logout, withdraw, checkPW, update, setCharges, getCharges, 
    setPaid, getStatement, getUsers, getAdminCharges, getSearch, setAdmin, upCash,
    downCash, createLotto, setStatus, setWinNumber,
};