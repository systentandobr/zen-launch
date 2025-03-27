#!/bin/bash

# Script 13 criado automaticamente
echo "Executando claude-ai13.sh"
cat << 'EOF' > src/core/infrastructure/native/AppModule.ts
/**
 * Módulo para interagir com os módulos nativos específicos do Launcher
 */
import { NativeModules, NativeEventEmitter, EmitterSubscription, Platform } from 'react-native';

const {
  AppUsageStatsModule,
  LauncherIntegrationModule,
  NotificationListenerModule,
  SystemSettingsModule,
  PackageEventsModule
} = NativeModules;

export interface AppInfo {
  id: string;
  name: string;
  packageName: string;
  category?: string;
  isRestricted: boolean;
  icon?: any;
}

export interface AppUsage {
  packageName: string;
  appName: string;
  usageTime: number; // em minutos
  lastUsed: number; // timestamp
}

export interface Notification {
  id: string;
  appName: string;
  packageName: string;
  title: string;
  content: string;
  timestamp: number;
  isRead?: boolean;
}

/**
 * API para funcionalidades relacionadas a aplicativos
 */
export const AppService = {
  /**
   * Obtém a lista de aplicativos instalados
   */
  getInstalledApps: async (): Promise<AppInfo[]> => {
    if (Platform.OS !== 'android') {
      return [];
    }
    
    try {
      return await LauncherIntegrationModule.getInstalledApps();
    } catch (error) {
      console.error('Erro ao obter apps instalados:', error);
      return [];
    }
  },

  /**
   * Lança um aplicativo pelo packageName
   */
  launchApp: async (packageName: string): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await LauncherIntegrationModule.launchApp(packageName);
    } catch (error) {
      console.error(`Erro ao lançar app ${packageName}:`, error);
      return false;
    }
  },

  /**
   * Obtém o ícone de um aplicativo
   */
  getAppIcon: async (packageName: string): Promise<string | null> => {
    if (Platform.OS !== 'android') {
      return null;
    }
    
    try {
      return await LauncherIntegrationModule.getAppIcon(packageName);
    } catch (error) {
      console.error(`Erro ao obter ícone para ${packageName}:`, error);
      return null;
    }
  },

  /**
   * Define o Zen Launcher como launcher padrão
   */
  setAsDefaultLauncher: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await LauncherIntegrationModule.setAsDefaultLauncher();
    } catch (error) {
      console.error('Erro ao definir como launcher padrão:', error);
      return false;
    }
  },

  /**
   * Registra um listener para eventos de alteração de pacotes
   */
  onPackageChanged: (
    callback: (event: string, data: { packageName: string, appName?: string }) => void
  ): EmitterSubscription | null => {
    if (Platform.OS !== 'android' || !PackageEventsModule) {
      return null;
    }
    
    const eventEmitter = new NativeEventEmitter(PackageEventsModule);
    
    const subscription = eventEmitter.addListener('packageChanged', callback);
    
    return subscription;
  }
};

/**
 * API para funcionalidades relacionadas a estatísticas de uso
 */
export const UsageStatsService = {
  /**
   * Obtém estatísticas de uso para um período
   */
  getUsageStats: async (timeStart: number, timeEnd: number): Promise<AppUsage[]> => {
    if (Platform.OS !== 'android') {
      return [];
    }
    
    try {
      return await AppUsageStatsModule.getUsageStats(timeStart, timeEnd);
    } catch (error) {
      console.error('Erro ao obter estatísticas de uso:', error);
      return [];
    }
  },

  /**
   * Verifica se a permissão de estatísticas de uso está concedida
   */
  isUsageStatsPermissionGranted: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await AppUsageStatsModule.isUsageStatsPermissionGranted();
    } catch (error) {
      console.error('Erro ao verificar permissão de estatísticas:', error);
      return false;
    }
  },

  /**
   * Solicita a permissão de estatísticas de uso
   */
  requestUsageStatsPermission: async (): Promise<void> => {
    if (Platform.OS !== 'android') {
      return;
    }
    
    try {
      await AppUsageStatsModule.requestUsageStatsPermission();
    } catch (error) {
      console.error('Erro ao solicitar permissão de estatísticas:', error);
    }
  }
};

/**
 * API para funcionalidades relacionadas a notificações
 */
export const NotificationService = {
  /**
   * Obtém as notificações ativas
   */
  getActiveNotifications: async (): Promise<Notification[]> => {
    if (Platform.OS !== 'android') {
      return [];
    }
    
    try {
      return await NotificationListenerModule.getActiveNotifications();
    } catch (error) {
      console.error('Erro ao obter notificações ativas:', error);
      return [];
    }
  },

  /**
   * Verifica se a permissão de listener de notificações está concedida
   */
  isNotificationListenerEnabled: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await NotificationListenerModule.isNotificationListenerEnabled();
    } catch (error) {
      console.error('Erro ao verificar permissão de notificações:', error);
      return false;
    }
  },

  /**
   * Abre as configurações de listener de notificações
   */
  openNotificationListenerSettings: async (): Promise<void> => {
    if (Platform.OS !== 'android') {
      return;
    }
    
    try {
      await NotificationListenerModule.openNotificationListenerSettings();
    } catch (error) {
      console.error('Erro ao abrir configurações de notificações:', error);
    }
  },

  /**
   * Dispensa uma notificação
   */
  dismissNotification: async (id: string): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await NotificationListenerModule.dismissNotification(id);
    } catch (error) {
      console.error(`Erro ao dispensar notificação ${id}:`, error);
      return false;
    }
  },

  /**
   * Registra um listener para eventos de notificações
   */
  onNotificationsChanged: (
    callback: (notifications: Notification[]) => void
  ): EmitterSubscription | null => {
    if (Platform.OS !== 'android' || !NotificationListenerModule) {
      return null;
    }
    
    const eventEmitter = new NativeEventEmitter(NotificationListenerModule);
    
    const subscription = eventEmitter.addListener(
      NotificationListenerModule.NOTIFICATIONS_EVENT, 
      callback
    );
    
    return subscription;
  }
};

/**
 * API para funcionalidades relacionadas a configurações do sistema
 */
export const SystemService = {
  /**
   * Define a orientação da tela
   */
  setScreenOrientation: async (orientation: 'portrait' | 'landscape' | 'auto'): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.setScreenOrientation(orientation);
    } catch (error) {
      console.error(`Erro ao definir orientação ${orientation}:`, error);
      return false;
    }
  },

  /**
   * Abre as configurações de Wi-Fi
   */
  openWifiSettings: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.openWifiSettings();
    } catch (error) {
      console.error('Erro ao abrir configurações de Wi-Fi:', error);
      return false;
    }
  },

  /**
   * Abre as configurações de Bluetooth
   */
  openBluetoothSettings: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.openBluetoothSettings();
    } catch (error) {
      console.error('Erro ao abrir configurações de Bluetooth:', error);
      return false;
    }
  },

  /**
   * Abre as configurações de Localização
   */
  openLocationSettings: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.openLocationSettings();
    } catch (error) {
      console.error('Erro ao abrir configurações de Localização:', error);
      return false;
    }
  },

  /**
   * Verifica se a localização está habilitada
   */
  isLocationEnabled: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.isLocationEnabled();
    } catch (error) {
      console.error('Erro ao verificar status da localização:', error);
      return false;
    }
  },

  /**
   * Abre as configurações do sistema
   */
  openSystemSettings: async (): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.openSystemSettings();
    } catch (error) {
      console.error('Erro ao abrir configurações do sistema:', error);
      return false;
    }
  },

  /**
   * Abre as informações de um aplicativo
   */
  openAppInfo: async (packageName: string): Promise<boolean> => {
    if (Platform.OS !== 'android') {
      return false;
    }
    
    try {
      return await SystemSettingsModule.openAppInfo(packageName);
    } catch (error) {
      console.error(`Erro ao abrir info do app ${packageName}:`, error);
      return false;
    }
  }
};

export default {
  AppService,
  UsageStatsService,
  NotificationService,
  SystemService
};
EOF