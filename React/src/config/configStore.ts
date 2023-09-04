import { combineReducers, configureStore } from "@reduxjs/toolkit";
import storage from "redux-persist/lib/storage";
import userIfReducer from "../modules/userIfSlice";
import refreshTokenReducer from "../modules/refreshTokenSlice";
import { persistReducer, persistStore } from "redux-persist";
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
    refreshToken: refreshTokenReducer,
});

const persistRootReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
    reducer: persistRootReducer,
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }),
    devTools: true,
});

const persistor = persistStore(store);

const resetPersistor = () => {
  try {
    persistor.purge();
  } catch (error) {
    console.error("localStorage 초기화 오류", error);
  }
};

export type RootState = ReturnType<typeof rootReducer>;
export { store, persistor, resetPersistor };