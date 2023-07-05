import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    cash: 0,
    nickname: "",
}

const userIfSlice = createSlice({
    name: "userIf",
    initialState,
    reducers: {
        setUserIf: (state, action) => {
            state.cash = action.payload.cash;
            state.nickname = action.payload.nickname;
        },
        logoutUser: (state) => {
            state.cash = 0;
            state.nickname = "";
        }
    }
})

export const { setUserIf, logoutUser } = userIfSlice.actions;
export default userIfSlice.reducer;