import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    email: "",
    nickname: "",
    cash: 0,
    role: "",
    statement: {},
}

const userIfSlice = createSlice({
    name: "userIf",
    initialState,
    reducers: {
        setUserIf: (state, action) => {
            const { email, nickname, cash, role, statement } = action.payload;
            state.email = email;
            state.nickname = nickname;
            state.cash = cash;
            state.role = role;
            state.statement = statement;
        },
        logoutUser: () => initialState,
    }
})

export const { setUserIf, logoutUser } = userIfSlice.actions;
export default userIfSlice.reducer;