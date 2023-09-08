import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { PURGE } from "redux-persist";

export type RefershSlice = {
    value: string,
    expirationTime: string,
}

const initialState: RefershSlice = {
    value: "",
    expirationTime: "",
}

const refreshTokenSlice = createSlice({
    name: "refreshToken",
    initialState,
    reducers: {
        setRefreshToken: (state, action: PayloadAction<RefershSlice>) => {
            state.value = action.payload.value;
            state.expirationTime = action.payload.expirationTime;
        },
    },

    extraReducers: (builder) => { builder.addCase(PURGE, () => initialState); }
})

export const { setRefreshToken } = refreshTokenSlice.actions;
export default refreshTokenSlice.reducer;