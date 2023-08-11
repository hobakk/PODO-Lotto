import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface UserIfState {
    email: string;
    nickname: string;
    cash: string;
    role: string;
    status: string;
    statement: Record<string, any>;
}

const initialState: UserIfState = {
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
        setUserIf: (state, action: PayloadAction<UserIfState>) => {
            const { email, nickname, cash, role, status, statement } = action.payload;
            state.email = email;
            state.nickname = nickname;
            state.cash = cash;
            state.role = role;
            state.status = status;
            state.statement = statement;
        },
        setStatus: (state, action: PayloadAction<string>) => {
            state.status = action.payload;
        },
        setRole: (state, action: PayloadAction<string>) => {
            state.role = action.payload;
        },
        setCashNickname: (state, action: PayloadAction<{ cash: string; nickname: string }>) => {
            const { cash, nickname } = action.payload;
            state.cash = cash;
            state.nickname = nickname;
        },
        logoutUser: () => initialState,
    }
})

export const { setUserIf, logoutUser, setStatus, setRole, setCashNickname } = userIfSlice.actions;
export default userIfSlice.reducer;