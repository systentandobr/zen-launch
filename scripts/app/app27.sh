#!/bin/bash

# Script 27 criado automaticamente
echo "Executando claude-ai27.sh"
cat << 'EOF' > src/modules/launcher/hooks/useSettings.ts
import { useAppSelector, useAppDispatch } from '@app/hooks';
import {
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
  ThemeMode,
  AppOrientation
} from '@state/slices/settingsSlice';
import SystemSettingsModule from '@core/infrastructure/native/SystemSettingsModule';

export const useSettings = () => {
  const dispatch = useAppDispatch();
  const settings = useAppSelector(state => state.settings);

  const updateThemeMode = (mode: ThemeMode) => {
    dispatch(setThemeMode(mode));
  };

  const updateShowClock = () => {
    dispatch(toggleShowClock());
  };

  const update24HourFormat = () => {
    dispatch(toggle24HourFormat());
  };

  const updateShowSeconds = () => {
    dispatch(toggleShowSeconds());
  };

  const updateShowWeather = () => {
    dispatch(toggleShowWeather());
  };

  const updateWeatherLocation = (location: string | null) => {
    dispatch(setWeatherLocation(location));
  };

  const updateEnableFocusMode = () => {
    dispatch(toggleEnableFocusMode());
  };

  const updateShowNotifications = () => {
    dispatch(toggleShowNotifications());
  };

  const updateAppOrientation = async (orientation: AppOrientation) => {
    try {
      await SystemSettingsModule.setScreenOrientation(orientation);
      dispatch(setAppOrientation(orientation));
      return true;
    } catch (error) {
      console.error('Erro ao atualizar orientação do app:', error);
      return false;
    }
  };

  const restoreDefaults = () => {
    dispatch(resetSettings());
    // Reestabelece a orientação do dispositivo para portrait
    SystemSettingsModule.setScreenOrientation('portrait');
  };

  return {
    settings,
    updateThemeMode,
    updateShowClock,
    update24HourFormat,
    updateShowSeconds,
    updateShowWeather,
    updateWeatherLocation,
    updateEnableFocusMode,
    updateAppOrientation,
    updateShowNotifications,
    restoreDefaults
  };
};

export default useSettings;
EOF