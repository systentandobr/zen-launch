#!/bin/bash

# Script 23 criado automaticamente
echo "Executando claude-ai23.sh"
cat << 'EOF' > src/modules/launcher/hooks/useApps.ts
import { useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '@app/hooks';
import { 
  fetchAppsStart, 
  fetchAppsSuccess, 
  fetchAppsFailure, 
  toggleAppRestriction,
  setAppRestriction 
} from '@state/slices/appsSlice';
import AppService from '@core/infrastructure/services/AppService';

export const useApps = () => {
  const dispatch = useAppDispatch();
  const { installedApps, isLoading, error } = useAppSelector(state => state.apps);

  useEffect(() => {
    const loadApps = async () => {
      try {
        dispatch(fetchAppsStart());
        const apps = await AppService.getInstalledApps();
        dispatch(fetchAppsSuccess(apps));
      } catch (error) {
        dispatch(fetchAppsFailure(error instanceof Error ? error.message : 'Erro ao carregar apps'));
      }
    };

    loadApps();
  }, [dispatch]);

  const launchApp = async (packageName: string) => {
    try {
      await AppService.launchApp(packageName);
      return true;
    } catch (error) {
      console.error('Erro ao abrir app:', error);
      return false;
    }
  };

  const toggleRestriction = (packageName: string) => {
    dispatch(toggleAppRestriction(packageName));
  };

  const setRestriction = (packageName: string, isRestricted: boolean) => {
    dispatch(setAppRestriction({ packageName, isRestricted }));
  };

  return {
    apps: installedApps,
    isLoading,
    error,
    launchApp,
    toggleRestriction,
    setRestriction
  };
};
EOF

cat << 'EOF' > src/modules/focus/hooks/useFocusMode.ts
import { useEffect, useState } from 'react';
import { useAppSelector, useAppDispatch } from '@app/hooks';
import {
  startFocusMode,
  stopFocusMode,
  addRestrictedApp,
  removeRestrictedApp,
  updateAppUsage
} from '@state/slices/focusSlice';
import UsageStatsService from '@core/infrastructure/services/UsageStatsService';

export const useFocusMode = () => {
  const dispatch = useAppDispatch();
  const focusState = useAppSelector(state => state.focus);
  const [timeRemaining, setTimeRemaining] = useState<number | undefined>(
    focusState.endTime && focusState.isActive 
      ? Math.max(0, Math.floor((focusState.endTime - Date.now()) / 1000)) 
      : undefined
  );

  // Efeito para atualizar o timer quando o modo foco estiver ativo
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (focusState.isActive && focusState.endTime) {
      interval = setInterval(() => {
        const now = Date.now();
        if (now >= focusState.endTime!) {
          dispatch(stopFocusMode());
          setTimeRemaining(undefined);
          clearInterval(interval);
        } else {
          const remaining = Math.floor((focusState.endTime! - now) / 1000);
          setTimeRemaining(remaining);
        }
      }, 1000);
    } else {
      setTimeRemaining(undefined);
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [dispatch, focusState.isActive, focusState.endTime]);

  // Efeito para atualizar as estatísticas de uso periodicamente
  useEffect(() => {
    const updateUsageStats = async () => {
      try {
        const now = Date.now();
EOF