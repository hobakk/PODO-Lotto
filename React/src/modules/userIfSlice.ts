import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserIfState } from "../shared/TypeMenu";
import { CashNicknameDto } from "../api/userApi";

const initialState: UserIfState = {
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
        setCashNickname: (state, action: PayloadAction<CashNicknameDto>) => {
            const { cash, nickname } = action.payload;
            state.cash = cash;
            state.nickname = nickname;
        },
        logoutUser: () => initialState,
    }
})

export const { setUserIf, logoutUser, setStatus, setRole, setCashNickname } = userIfSlice.actions;
export default userIfSlice.reducer;