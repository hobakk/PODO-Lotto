import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    email: "",
    nickname: "",
    cash: 0,
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
            console.log(action.payload)
            state.status = action.payload;
        },
        logoutUser: () => initialState,
    }
})

export const { setUserIf, logoutUser, setStatus } = userIfSlice.actions;
export default userIfSlice.reducer;