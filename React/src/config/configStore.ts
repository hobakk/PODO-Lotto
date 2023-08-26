import { combineReducers, configureStore } from "@reduxjs/toolkit";
import storage from "redux-persist/lib/storage";
import userIfReducer from "../modules/userIfSlice";
import { persistReducer, persistStore } from "redux-persist";

const persistConfig= {
    key: "root",
    storage,
};

const rootReducer = combineReducers({
    userIf: userIfReducer,
})

const persistRootReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
    reducer: persistRootReducer,
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }),
    devTools: true,
});

const persistor = persistStore(store);

export type RootState = ReturnType<typeof rootReducer>;
export { store, persistor };