#!/bin/bash

# Script 10 criado automaticamente
echo "Executando claude-ai10.sh"
cat << 'EOF' > src/modules/launcher/components/Notifications.tsx
import React from 'react';
import { View, FlatList, Pressable } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import { useTheme } from '@core/ui/theme/ThemeContext';

export interface Notification {
  id: string;
  appName: string;
  packageName: string;
  title: string;
  content: string;
  timestamp: number; // Unix timestamp
  icon?: any;
  isRead?: boolean;
}

interface NotificationsProps {
  notifications: Notification[];
  onNotificationPress: (notification: Notification) => void;
  onDismiss?: (id: string) => void;
  className?: string;
}

const Notifications: React.FC<NotificationsProps> = ({
  notifications,
  onNotificationPress,
  onDismiss,
  className = '',
}) => {
  const { isDark } = useTheme();
  
  const formatTime = (timestamp: number) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };
  
  const renderNotification = ({ item }: { item: Notification }) => (
    <Pressable
      className={`p-3 mb-2 rounded-lg ${
        isDark ? 'bg-gray-800' : 'bg-gray-100'
      } ${item.isRead ? 'opacity-60' : ''}`}
      onPress={() => onNotificationPress(item)}
    >
      <View className="flex-row justify-between items-start">
        <Text className="font-semibold flex-1">{item.appName}</Text>
        <Text variant="caption">{formatTime(item.timestamp)}</Text>
      </View>
      
      <Text className="font-medium mt-1">{item.title}</Text>
      <Text numberOfLines={2} className="mt-1">{item.content}</Text>
      
      {onDismiss && (
        <Pressable 
          className="absolute top-2 right-2 p-2" 
          onPress={() => onDismiss(item.id)}
          hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
        >
          <Text>✕</Text>
        </Pressable>
      )}
    </Pressable>
  );

  return (
    <Card className={`${className}`}>
      <View className="flex-row justify-between items-center mb-4">
        <Text variant="h3">Notificações</Text>
        {notifications.length > 0 && (
          <Text className="text-primary">{notifications.length}</Text>
        )}
      </View>
      
      {notifications.length > 0 ? (
        <FlatList
          data={notifications}
          renderItem={renderNotification}
          keyExtractor={item => item.id}
          scrollEnabled={false}
        />
      ) : (
        <Text className="text-center py-4 italic opacity-50">
          Nenhuma notificação
        </Text>
      )}
    </Card>
  );
};

export default Notifications;
EOF