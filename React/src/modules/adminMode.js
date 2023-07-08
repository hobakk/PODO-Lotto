import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    mode: "",
}

const adminModeSlice = createSlice({
    name: "adminMode",
    initialState,
    reducers: {
        setAdminMode: (state, action) => {
            state.mode = action.payload;
        }
    }
})

export const { setAdminMode } = adminModeSlice.actions;
export default adminModeSlice.reducer;