import { combineReducers, configureStore } from "@reduxjs/toolkit";
import userIfReducer from "../modules/userIfSlice";

const store = configureStore({
    reducer: {
        userIf: userIfReducer,
    },
});

const rootReducer = combineReducers({
    userIf: userIfReducer,
})

export type RootState = ReturnType<typeof rootReducer>;
export default store;