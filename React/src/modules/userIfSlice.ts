import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserDetailInfo } from "../shared/TypeMenu";
import { CashNicknameDto } from "../api/userApi";
import { PURGE } from "redux-persist";

const initialState: UserDetailInfo = {
    userId: 0,
    email: "",
    nickname: "",
    cash: 0,
    role: "",
    status: "",
}

const userIfSlice = createSlice({
    name: "userIf",
    initialState,
    reducers: {
        setUserIf: (state, action: PayloadAction<UserDetailInfo>) => {
            const { userId, email, nickname, cash, role, status } = action.payload;
            state.userId = userId;
            state.email = email;
            state.nickname = nickname;
            state.cash = cash;
            state.role = role;
            state.status = status;
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
    },

    extraReducers: (builder) => { builder.addCase(PURGE, () => initialState); }
})

export const { setUserIf, setStatus, setRole, setCashNickname } = userIfSlice.actions;
export default userIfSlice.reducer;