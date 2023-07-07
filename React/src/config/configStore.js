import { configureStore } from "@reduxjs/toolkit";
import userIfReducer from "../modules/userIfSlice";

const store = configureStore({
    reducer: {
        userIf: userIfReducer,
    },
});

export default store;