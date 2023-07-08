import { configureStore } from "@reduxjs/toolkit";
import userIfReducer from "../modules/userIfSlice";
import adminModeReducer from "../modules/adminMode";

const store = configureStore({
    reducer: {
        userIf: userIfReducer,
        adminMode: adminModeReducer,
    },
});

export default store;