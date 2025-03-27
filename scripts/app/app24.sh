#!/bin/bash

# Script 24 criado automaticamente
echo "Executando claude-ai24.sh"
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
        const startTime = now - (24 * 60 * 60 * 1000); // Últimas 24 horas
        const usageStats = await UsageStatsService.getUsageStats(startTime, now);
        dispatch(updateAppUsage(usageStats));
      } catch (error) {
        console.error('Erro ao obter estatísticas de uso:', error);
      }
    };

    updateUsageStats();
    
    const interval = setInterval(updateUsageStats, 30 * 60 * 1000); // Atualiza a cada 30 minutos
    
    return () => clearInterval(interval);
  }, [dispatch]);

  const checkPermissions = async (): Promise<boolean> => {
    return await UsageStatsService.isUsageStatsPermissionGranted();
  };

  const requestPermissions = async (): Promise<void> => {
    await UsageStatsService.requestUsageStatsPermission();
  };

  const startFocus = (durationMinutes: number) => {
    dispatch(startFocusMode(durationMinutes));
  };

  const stopFocus = () => {
    dispatch(stopFocusMode());
  };

  const addRestricted = (packageName: string) => {
    dispatch(addRestrictedApp(packageName));
  };

  const removeRestricted = (packageName: string) => {
    dispatch(removeRestrictedApp(packageName));
  };

  return {
    isActive: focusState.isActive,
    timeRemaining,
    restrictedApps: focusState.restrictedApps,
    appUsage: focusState.appUsage,
    dailyLimits: focusState.dailyLimits,
    startFocus,
    stopFocus,
    addRestricted,
    removeRestricted,
    checkPermissions,
    requestPermissions
  };
};

export default useFocusMode;
EOF