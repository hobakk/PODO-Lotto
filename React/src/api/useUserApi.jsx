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
    return data;
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
        const { data } = await api.patch("/users/paid", msg);
        return data;
    } catch (error) {
        throw error; 
    }
}
    
const getStatement = async () => {
    try {
        const { data } = await api.get("/users/statement");
        return data;
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

const getSearch = async ({ msg, cash }) => {
    const { data } = await api.get("/admin/search", { params: {
        msg,
        cash,
    }});
    console.log(data)
    return data;
}

const setAdmin = async ({ userId, msg }) => {
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
    return data;
}

const setStatusFromAdmin = async ({ userId, msg }) => {
    const { data } = await api.patch(`/admin/status/${userId}`, msg);
    return data.code;
}

const setWinNumber = async ( inputValue ) => {
    try {
        const { data } = await api.post("/winnumber/set", inputValue);
        return data;
    } catch (error) {
        throw error;
    }
}

const setRoleFromAdmin = async ({ userId, msg }) => {
    try {
        const { data } = await api.patch(`/admin/role/${userId}`, msg);
        return data.code;
    } catch (error) {
        throw error;
    }
}

// Lotto ( 통계 )
const getMainTopNumber = async () => {
    try {
        const { data } = await api.get("/lotto/main");
        return data.data;
    } catch (error) {
        throw error;
    }
}

const getTopNumberForMonth = async (yearMonth) => {
    try {
        const { data } = await api.get("/lotto/yearMonth", {
            params: {
                yearMonth,
            }
        });
        return data.data;
    } catch (error) {
        throw error;
    }
}

const getAllMonthStats = async () => {
    try {
        const { data } = await api.get("/lotto/yearMonth/all");
        return data.data;   
    } catch (error) {
        throw error;
    }
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

const getRecentNumber = async () => {
    try {
        const res = await api.get("/sixnum/recent")
        return res.data.data;
    } catch (error) {
        throw error;
    }
}

export { 
    getInformation, logout, withdraw, checkPW, update, setCharges, getCharges, 
    setPaid, getStatement, getUsers, getAdminCharges, getSearch, setAdmin, upCash,
    downCash, createLotto, setStatusFromAdmin, setWinNumber, getMainTopNumber, 
    getTopNumberForMonth, buyNumber, statisticalNumber, getCashNickname,
    getRecentNumber, getAllMonthStats, setRoleFromAdmin
};