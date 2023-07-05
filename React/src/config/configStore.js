import { configureStore, getDefaultMiddleware } from "@reduxjs/toolkit";
import userIfReducer from "../modules/userIfSlice";
import tokenMiddleware from "../middleware/tokenMiddleware";
import accessTokenReducer from "../modules/accessTokenSlice";

const store = configureStore({
    reducer: {
        userIf: userIfReducer,
        accessToken: accessTokenReducer,
    },
    middleware: [...getDefaultMiddleware(), tokenMiddleware,]
});

export default store;