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
