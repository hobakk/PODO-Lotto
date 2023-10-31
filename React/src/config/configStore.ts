import { combineReducers, configureStore } from "@reduxjs/toolkit";
import storage from "redux-persist/lib/storage";
import userIfReducer from "../modules/userIfSlice";
import { persistReducer, persistStore, PURGE, PERSIST } from "redux-persist";
import { encryptTransform } from 'redux-persist-transform-encrypt';
import { PersistConfig, Transform } from "redux-persist/es/types";

const secretKey = process.env.REACT_APP_ENCRYPTION_KEY;

const encryptor: Transform<any, any> = encryptTransform({
  secretKey: secretKey!,
  onError: (error)=>{
    console.error("An error occurred:", error);
  },
});

const persistConfig: PersistConfig<any>= {
  key: "root",
  storage,
  transforms: [encryptor],
};

const rootReducer = combineReducers({
  userIf: userIfReducer,
});

const persistRootReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
  reducer: persistRootReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({
    serializableCheck: {
        ignoredActions: [PERSIST, PURGE],
    },
  }),
  devTools: true,
});

const persistor = persistStore(store);

export type RootState = ReturnType<typeof rootReducer>;
export { store, persistor };