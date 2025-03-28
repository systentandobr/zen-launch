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
