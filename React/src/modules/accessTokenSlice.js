import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    accessToken: "",
}

const accessTokenSlice =  createSlice({
    name:"accessToken",
    initialState,
    reducers: {
        setAccessToken: (state, action) => {
            state.accessToken = action.payload;
        },
        logoutToken: (state) => {
            state.accessToken = "";
        }
    }
})

export const { setAccessToken, logoutToken } = accessTokenSlice.actions;
export default accessTokenSlice.reducer;