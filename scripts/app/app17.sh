#!/bin/bash

# Script 17 criado automaticamente
echo "Executando claude-ai17.sh"
cat << 'EOF' > src/app/store.ts
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
EOF

cat << 'EOF' > src/app/hooks.ts
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from './store';

// Use em todo o app ao invés de useDispatch e useSelector normais
export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
EOF