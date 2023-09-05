import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { PURGE } from "redux-persist";

const initialState: {refreshToken: string} = {
    refreshToken: "",
}

const refreshTokenSlice = createSlice({
    name: "refreshToken",
    initialState,
    reducers: {
        setRefreshToken: (state, action: PayloadAction<string>) => {
            state.refreshToken = action.payload;
        },
    },

    extraReducers: (builder) => { builder.addCase(PURGE, () => initialState); }
})

export const { setRefreshToken } = refreshTokenSlice.actions;
export default refreshTokenSlice.reducer;