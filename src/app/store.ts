import { configureStore } from '@reduxjs/toolkit';
import appsReducer from '@state/slices/appsSlice';
import focusReducer from '@state/slices/focusSlice';
import settingsReducer from '@state/slices/settingsSlice';

const store = configureStore({
  reducer: {
    apps: appsReducer,
    focus: focusReducer,
    settings: settingsReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
