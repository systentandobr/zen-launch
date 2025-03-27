#!/bin/bash

# Script 34 criado automaticamente
echo "Executando claude-ai34.sh"
cat << 'EOF' > src/core/hooks/usePermissions.ts
import { useState, useEffect } from 'react';
import { NativeModules, Platform, Alert } from 'react-native';
import { PERMISSIONS, RESULTS, check, request } from 'react-native-permissions';

const { UsageStatsModule, NotificationListenerModule } = NativeModules;

interface PermissionStatus {
  usageStats: boolean;
  notifications: boolean;
  location: boolean;
}

export const usePermissions = () => {
  const [status, setStatus] = useState<PermissionStatus>({
    usageStats: false,
    notifications: false,
    location: false
  });

  const checkAllPermissions = async () => {
    try {
      // Verificar permissão de estatísticas de uso
      const usageStats = await UsageStatsModule.isUsageStatsPermissionGranted();

      // Verificar permissão de notificações
      const notifications = await NotificationListenerModule.isNotificationListenerEnabled();

      // Verificar permissão de localização
      const locationPermission = Platform.OS === 'ios' 
        ? PERMISSIONS.IOS.LOCATION_WHEN_IN_USE 
        : PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
      
      const locationStatus = await check(locationPermission);
      const location = locationStatus === RESULTS.GRANTED;

      setStatus({
        usageStats,
        notifications,
        location
      });

      return { usageStats, notifications, location };
    } catch (error) {
      console.error('Erro ao verificar permissões:', error);
      return status;
    }
  };

  useEffect(() => {
    checkAllPermissions();
  }, []);

  const requestUsageStatsPermission = async () => {
    try {
      await UsageStatsModule.requestUsageStatsPermission();
      // A verificação será feita quando o usuário retornar ao app
      return true;
    } catch (error) {
      console.error('Erro ao solicitar permissão de estatísticas de uso:', error);
      return false;
    }
  };

  const requestNotificationPermission = async () => {
    try {
      await NotificationListenerModule.openNotificationListenerSettings();
      // A verificação será feita quando o usuário retornar ao app
      return true;
    } catch (error) {
      console.error('Erro ao solicitar permissão de notificações:', error);
      return false;
    }
  };

  const requestLocationPermission = async () => {
    try {
      const locationPermission = Platform.OS === 'ios' 
        ? PERMISSIONS.IOS.LOCATION_WHEN_IN_USE 
        : PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
      
      const result = await request(locationPermission);
      const granted = result === RESULTS.GRANTED;
      
      setStatus(prev => ({
        ...prev,
        location: granted
      }));
      
      return granted;
    } catch (error) {
      console.error('Erro ao solicitar permissão de localização:', error);
      return false;
    }
  };

  const requestPermission = async (type: keyof PermissionStatus) => {
    switch (type) {
      case 'usageStats':
        return requestUsageStatsPermission();
      case 'notifications':
        return requestNotificationPermission();
      case 'location':
        return requestLocationPermission();
      default:
        return false;
    }
  };

  const showPermissionDialog = (
    type: keyof PermissionStatus, 
    title: string, 
    message: string
  ) => {
    Alert.alert(
      title,
      message,
      [
        { text: "Cancelar", style: "cancel" },
        { text: "Permitir", onPress: () => requestPermission(type) }
      ]
    );
  };

  return {
    status,
    checkAllPermissions,
    requestPermission,
    showPermissionDialog
  };
};

export default usePermissions;
EOF