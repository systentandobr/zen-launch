#!/bin/bash

# Script 16 criado automaticamente
echo "Executando claude-ai16.sh"
cat << 'EOF' > src/state/slices/appsSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface AppInfo {
  id: string;
  name: string;
  packageName: string;
  category?: string;
  isRestricted: boolean;
}

interface AppsState {
  installedApps: AppInfo[];
  isLoading: boolean;
  error: string | null;
}

const initialState: AppsState = {
  installedApps: [],
  isLoading: false,
  error: null,
};

const appsSlice = createSlice({
  name: 'apps',
  initialState,
  reducers: {
    fetchAppsStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    fetchAppsSuccess: (state, action: PayloadAction<AppInfo[]>) => {
      state.installedApps = action.payload;
      state.isLoading = false;
    },
    fetchAppsFailure: (state, action: PayloadAction<string>) => {
      state.isLoading = false;
      state.error = action.payload;
    },
    toggleAppRestriction: (state, action: PayloadAction<string>) => {
      const packageName = action.payload;
      const appIndex = state.installedApps.findIndex(app => app.packageName === packageName);
      
      if (appIndex !== -1) {
        state.installedApps[appIndex].isRestricted = !state.installedApps[appIndex].isRestricted;
      }
    },
    setAppRestriction: (state, action: PayloadAction<{ packageName: string, isRestricted: boolean }>) => {
      const { packageName, isRestricted } = action.payload;
      const appIndex = state.installedApps.findIndex(app => app.packageName === packageName);
      
      if (appIndex !== -1) {
        state.installedApps[appIndex].isRestricted = isRestricted;
      }
    },
  },
});

export const {
  fetchAppsStart,
  fetchAppsSuccess,
  fetchAppsFailure,
  toggleAppRestriction,
  setAppRestriction,
} = appsSlice.actions;

export default appsSlice.reducer;
EOF

cat << 'EOF' > src/state/slices/focusSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface AppUsage {
  packageName: string;
  appName: string;
  usageTime: number; // em minutos
  lastUsed: number; // timestamp
}

interface FocusState {
  isActive: boolean;
  startTime: number | null; // timestamp
  endTime: number | null; // timestamp
  duration: number; // em minutos
  restrictedApps: string[]; // lista de packageNames
  appUsage: AppUsage[];
  dailyLimits: Record<string, number>; // packageName -> minutos
}

const initialState: FocusState = {
  isActive: false,
  startTime: null,
  endTime: null,
  duration: 60, // padrão: 60 minutos
  restrictedApps: [],
  appUsage: [],
  dailyLimits: {},
};

const focusSlice = createSlice({
  name: 'focus',
  initialState,
  reducers: {
    startFocusMode: (state, action: PayloadAction<number>) => {
      state.isActive = true;
      state.startTime = Date.now();
      state.duration = action.payload;
      state.endTime = state.startTime + action.payload * 60 * 1000;
    },
    stopFocusMode: (state) => {
      state.isActive = false;
      state.startTime = null;
      state.endTime = null;
    },
    addRestrictedApp: (state, action: PayloadAction<string>) => {
      if (!state.restrictedApps.includes(action.payload)) {
        state.restrictedApps.push(action.payload);
      }
    },
    removeRestrictedApp: (state, action: PayloadAction<string>) => {
      state.restrictedApps = state.restrictedApps.filter(app => app !== action.payload);
    },
    updateAppUsage: (state, action: PayloadAction<AppUsage[]>) => {
      state.appUsage = action.payload;
    },
    setDailyLimit: (state, action: PayloadAction<{ packageName: string, minutes: number }>) => {
      const { packageName, minutes } = action.payload;
      state.dailyLimits[packageName] = minutes;
    },
    removeDailyLimit: (state, action: PayloadAction<string>) => {
      delete state.dailyLimits[action.payload];
    },
  },
});

export const {
  startFocusMode,
  stopFocusMode,
  addRestrictedApp,
  removeRestrictedApp,
  updateAppUsage,
  setDailyLimit,
  removeDailyLimit,
} = focusSlice.actions;

export default focusSlice.reducer;
EOF

cat << 'EOF' > src/state/slices/settingsSlice.ts
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
EOF