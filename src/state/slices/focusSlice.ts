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
