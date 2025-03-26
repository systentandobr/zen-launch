#!/bin/bash

# Script para criar a estrutura de diretórios do projeto Zen Launcher
# Executar este script na raiz do projeto após fazer o 'npx react-native init' inicial

echo "Criando estrutura de diretórios para o Zen Launcher..."

# Diretórios principais
mkdir -p src/app
mkdir -p src/core/ui/components
mkdir -p src/core/ui/theme
mkdir -p src/core/hooks
mkdir -p src/core/infrastructure/services
mkdir -p src/core/infrastructure/native
mkdir -p src/core/utils

# Diretórios de módulos
mkdir -p src/modules/launcher/components
mkdir -p src/modules/launcher/screens
mkdir -p src/modules/launcher/hooks

mkdir -p src/modules/focus/components
mkdir -p src/modules/focus/screens
mkdir -p src/modules/focus/hooks

mkdir -p src/modules/tasks/components
mkdir -p src/modules/tasks/screens
mkdir -p src/modules/tasks/hooks

mkdir -p src/modules/weather/components
mkdir -p src/modules/weather/hooks

mkdir -p src/modules/notifications/components
mkdir -p src/modules/notifications/hooks

# Diretórios de navegação
mkdir -p src/navigation

# Diretórios de gerenciamento de estado
mkdir -p src/state/slices
mkdir -p src/state/selectors

# Diretórios para tipos globais
mkdir -p src/types

# Diretórios para módulos nativos Android
mkdir -p android/app/src/main/java/com/zenlauncher

echo "Criando arquivos base iniciais..."

# Criar arquivos base para o app
cat << 'EOT' > src/app/store.ts
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
EOT

cat << 'EOT' > src/app/hooks.ts
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from './store';

// Use em todo o app ao invés de useDispatch e useSelector normais
export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
EOT

# Criar arquivo de tema básico
cat << 'EOT' > src/core/ui/theme/ThemeContext.tsx
import React, { createContext, useContext, useState, useEffect } from 'react';
import { useColorScheme } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

type ThemeMode = 'light' | 'dark' | 'system';

interface ThemeContextType {
  isDark: boolean;
  themeMode: ThemeMode;
  setThemeMode: (mode: ThemeMode) => void;
}

const ThemeContext = createContext<ThemeContextType>({
  isDark: false,
  themeMode: 'system',
  setThemeMode: () => {},
});

export const useTheme = () => useContext(ThemeContext);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const systemColorScheme = useColorScheme();
  const [themeMode, setThemeMode] = useState<ThemeMode>('system');
  const [isDark, setIsDark] = useState<boolean>(systemColorScheme === 'dark');

  useEffect(() => {
    const loadThemePreference = async () => {
      try {
        const savedTheme = await AsyncStorage.getItem('theme_mode');
        if (savedTheme === 'light' || savedTheme === 'dark' || savedTheme === 'system') {
          setThemeMode(savedTheme as ThemeMode);
        }
      } catch (error) {
        console.log('Error loading theme preference:', error);
      }
    };

    loadThemePreference();
  }, []);

  useEffect(() => {
    // Calculate if dark mode is active based on theme mode
    setIsDark(
      themeMode === 'system' 
        ? systemColorScheme === 'dark' 
        : themeMode === 'dark'
    );
    
    // Save theme preference
    AsyncStorage.setItem('theme_mode', themeMode);
  }, [themeMode, systemColorScheme]);

  return (
    <ThemeContext.Provider
      value={{
        isDark,
        themeMode,
        setThemeMode,
      }}
    >
      {children}
    </ThemeContext.Provider>
  );
};
EOT

# Criar arquivos de navegação básicos
cat << 'EOT' > src/navigation/types.ts
export type RootStackParamList = {
  Main: undefined;
  Settings: undefined;
  AppSettings: undefined;
  AppDetails: { packageName: string };
  TaskDetails: { taskId: string };
  Notifications: undefined;
  About: undefined;
};

export type MainTabParamList = {
  Home: undefined;
  Apps: undefined;
  Focus: undefined;
  Tasks: undefined;
  Settings: undefined;
};
EOT

# Criar App.tsx base
cat << 'EOT' > src/App.tsx
import React from 'react';
import { StatusBar, LogBox } from 'react-native';
import { Provider as ReduxProvider } from 'react-redux';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { ThemeProvider } from '@core/ui/theme/ThemeContext';
import RootNavigator from '@navigation/RootNavigator';
import store from '@app/store';

// Ignora warnings específicos
LogBox.ignoreLogs([
  'ViewPropTypes will be removed from React Native',
  'AsyncStorage has been extracted from react-native',
]);

const App: React.FC = () => {
  return (
    <ReduxProvider store={store}>
      <ThemeProvider>
        <SafeAreaProvider>
          <StatusBar translucent backgroundColor="transparent" />
          <RootNavigator />
        </SafeAreaProvider>
      </ThemeProvider>
    </ReduxProvider>
  );
};

export default App;
EOT

# Criar slices base para o Redux
cat << 'EOT' > src/state/slices/settingsSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export type ThemeMode = 'light' | 'dark' | 'system';
export type AppOrientation = 'portrait' | 'landscape' | 'auto';

interface SettingsState {
  themeMode: ThemeMode;
  showClock: boolean;
  is24HourFormat: boolean;
  showSeconds: boolean;
  showWeather: boolean;
  weatherLocation: string | null;
  enableFocusMode: boolean;
  appOrientation: AppOrientation;
  showNotifications: boolean;
}

const initialState: SettingsState = {
  themeMode: 'system',
  showClock: true,
  is24HourFormat: true,
  showSeconds: false,
  showWeather: true,
  weatherLocation: null,
  enableFocusMode: true,
  appOrientation: 'portrait',
  showNotifications: true,
};

const settingsSlice = createSlice({
  name: 'settings',
  initialState,
  reducers: {
    setThemeMode: (state, action: PayloadAction<ThemeMode>) => {
      state.themeMode = action.payload;
    },
    toggleShowClock: (state) => {
      state.showClock = !state.showClock;
    },
    toggle24HourFormat: (state) => {
      state.is24HourFormat = !state.is24HourFormat;
    },
    toggleShowSeconds: (state) => {
      state.showSeconds = !state.showSeconds;
    },
    toggleShowWeather: (state) => {
      state.showWeather = !state.showWeather;
    },
    setWeatherLocation: (state, action: PayloadAction<string | null>) => {
      state.weatherLocation = action.payload;
    },
    toggleEnableFocusMode: (state) => {
      state.enableFocusMode = !state.enableFocusMode;
    },
    setAppOrientation: (state, action: PayloadAction<AppOrientation>) => {
      state.appOrientation = action.payload;
    },
    toggleShowNotifications: (state) => {
      state.showNotifications = !state.showNotifications;
    },
    resetSettings: (state) => {
      return initialState;
    },
  },
});

export const {
  setThemeMode,
  toggleShowClock,
  toggle24HourFormat,
  toggleShowSeconds,
  toggleShowWeather,
  setWeatherLocation,
  toggleEnableFocusMode,
  setAppOrientation,
  toggleShowNotifications,
  resetSettings,
} = settingsSlice.actions;

export default settingsSlice.reducer;
EOT

# Criar index.js base
cat << 'EOT' > index.js
import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';

AppRegistry.registerComponent(appName, () => App);
EOT

echo "Estrutura de diretórios criada com sucesso!"
echo "Para configurar o TypeScript e Babel adequadamente, não esqueça de atualizar os arquivos tsconfig.json e babel.config.js conforme necessário."
echo "Execute 'yarn add' ou 'npm install' para adicionar as dependências necessárias ao projeto."
