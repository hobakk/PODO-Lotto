import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const initialState: {refreshToken: string} = {
    refreshToken: "",
}

const refreshTokenSlice = createSlice({
    name: "refreshToken",
    initialState,
    reducers: {
        setRefreshToken: (state, action: PayloadAction<{refreshToken: string}>) => {
            state.refreshToken = action.payload.refreshToken;
        },
        resetRefreshToken: () => initialState,
    }
})

export const { setRefreshToken, resetRefreshToken } = refreshTokenSlice.actions;
export default refreshTokenSlice.reducer;