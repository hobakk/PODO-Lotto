import { api } from "./config";

// User
const getInformation = async () => {
    const { data } = await api.get(`/users/my-information`);
    return data.data;
}

const logout = async () => {
    const { data } = await api.post(`/users/logout`);
    return data;
}

const getCashNickname = async () => {
    const { data } = await api.get("/users/cash");
    return data.data;
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
    try {
        const { data } = await api.post("/users/charging", inputValue);
        return data.code;
    } catch (error) {
        throw error;
    }
}

const getCharges = async () => {
    try {
        const { data } = await api.get("/users/charging");
        return data.data;    
    } catch (error) {
        throw error;
    }
}

const setPaid = async (msg) => {
    try {
        const { data } = await api.post("/users/paid", msg);
        return data.code;
    } catch (error) {
        throw error; 
    }
}
    
const getStatement = async () => {
    try {
        const { data } = await api.get("/users/statement");
        return data.data;
    } catch (error) {
        throw error;
    }
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

// Lotto ( 통계 )
const getMainTopNumber = async () => {
    const { data } = await api.get("/lotto/main");
    return data.data;
}

const getTopNumberForMonth = async (yearMonth) => {
    const { data } = await api.get("/lotto/yearMonth", yearMonth);
    return data.data;
}

// SixNumber ( 추천 번호 구매 )
const buyNumber = async (inputValue) => {
    try {
        const { data } = await api.post("/sixnum", inputValue);
        return data.data;
    } catch (error) {
        throw error;
    }
    
}

const statisticalNumber = async (inputValue) => {
    try {
        const { data } = await api.post("/sixnum/repetition", inputValue);
        return data.data;
    } catch (error) {
        throw error;
    }
    
}

export { 
    getInformation, logout, withdraw, checkPW, update, setCharges, getCharges, 
    setPaid, getStatement, getUsers, getAdminCharges, getSearch, setAdmin, upCash,
    downCash, createLotto, setStatus, setWinNumber, getMainTopNumber, 
    getTopNumberForMonth, buyNumber, statisticalNumber, getCashNickname
};