import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    email: "",
    nickname: "",
    cash: "",
    role: "",
    status: "",
    statement: {},
}

const userIfSlice = createSlice({
    name: "userIf",
    initialState,
    reducers: {
        setUserIf: (state, action) => {
            const { email, nickname, cash, role, status, statement } = action.payload;
            state.email = email;
            state.nickname = nickname;
            state.cash = cash;
            state.role = role;
            state.status = status;
            state.statement = statement;
        },
        setStatus: (state, action) => {
            state.status = action.payload;
        },
        setRole: (state, action) => {
            state.role = action.payload;
        },
        setCashNickname: (state, action) => {
            const { cash, nickname } = action.payload;
            if  (state.cash !== cash) {
                state.cash = cash;
            }
            if (state.nickname !== nickname) {
                state.nickname = nickname;
            }
        },
        logoutUser: () => initialState,
    }
})

export const { setUserIf, logoutUser, setStatus, setRole, setCashNickname } = userIfSlice.actions;
export default userIfSlice.reducer;