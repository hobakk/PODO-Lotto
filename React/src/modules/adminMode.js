import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    mode: "",
}

const adminModeSlice = createSlice({
    name: "adminMode",
    initialState,
    reducers: {
        setAdminMode: (state, action) => {
            console.log(action.payload)
            state.mode = action.payload;
        }
    }
})

export const { setAdminMode } = adminModeSlice.actions;
export default adminModeSlice.reducer;