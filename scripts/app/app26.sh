#!/bin/bash

# Script 26 criado automaticamente
echo "Executando claude-ai26.sh"
cat << 'EOF' > src/modules/notifications/hooks/useNotifications.ts
import { useState, useEffect } from 'react';
import { useAppSelector } from '@app/hooks';
import { NativeEventEmitter, NativeModules } from 'react-native';

const { NotificationListenerModule } = NativeModules;

export interface Notification {
  id: string;
  appName: string;
  packageName: string;
  title: string;
  content: string;
  timestamp: number;
  isRead?: boolean;
}

export const useNotifications = () => {
  const { showNotifications } = useAppSelector(state => state.settings);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isPermissionGranted, setIsPermissionGranted] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    // Verifica se a permissão para leitura de notificações está habilitada
    const checkPermission = async () => {
      try {
        const granted = await NotificationListenerModule.isNotificationListenerEnabled();
        setIsPermissionGranted(granted);
      } catch (error) {
        console.error('Erro ao verificar permissão de notificações:', error);
        setIsPermissionGranted(false);
      }
    };

    checkPermission();
  }, []);

  useEffect(() => {
    if (!showNotifications || !isPermissionGranted) {
      setNotifications([]);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);

    // Carrega notificações existentes
    const loadNotifications = async () => {
      try {
        const activeNotifications = await NotificationListenerModule.getActiveNotifications();
        setNotifications(activeNotifications);
        setIsLoading(false);
      } catch (error) {
        console.error('Erro ao carregar notificações:', error);
        setNotifications([]);
        setIsLoading(false);
      }
    };

    loadNotifications();

    // Configura listener para mudanças em notificações
    const eventEmitter = new NativeEventEmitter(NotificationListenerModule);
    const subscription = eventEmitter.addListener(
      NotificationListenerModule.NOTIFICATIONS_EVENT,
      (newNotifications: Notification[]) => {
        setNotifications(newNotifications);
      }
    );

    return () => {
      subscription.remove();
    };
  }, [showNotifications, isPermissionGranted]);

  const dismissNotification = async (id: string) => {
    try {
      await NotificationListenerModule.dismissNotification(id);
      setNotifications(prev => prev.filter(notification => notification.id !== id));
      return true;
    } catch (error) {
      console.error('Erro ao dispensar notificação:', error);
      return false;
    }
  };

  const markAsRead = (id: string) => {
    setNotifications(prev =>
      prev.map(notification =>
        notification.id === id
          ? { ...notification, isRead: true }
          : notification
      )
    );
  };

  const requestPermission = async () => {
    try {
      await NotificationListenerModule.openNotificationListenerSettings();
      return true;
    } catch (error) {
      console.error('Erro ao abrir configurações de notificações:', error);
      return false;
    }
  };

  return {
    notifications,
    isPermissionGranted,
    isLoading,
    dismissNotification,
    markAsRead,
    requestPermission
  };
};

export default useNotifications;
EOF