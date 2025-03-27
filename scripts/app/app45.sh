#!/bin/bash

# Script 45 criado automaticamente
echo "Executando claude-ai45.sh"
cat << 'EOF' > src/navigation/RootNavigator.tsx
import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import { RootStackParamList } from './types';
import MainTabNavigator from './MainTabNavigator';
import SettingsScreen from '@modules/launcher/screens/SettingsScreen';
import AppSettingsScreen from '@modules/launcher/screens/AppSettingsScreen';
import AboutScreen from '@modules/launcher/screens/AboutScreen';
import useLockScreen from '@modules/launcher/hooks/useLockScreen';
import LockScreen from '@modules/launcher/components/LockScreen';

const Stack = createStackNavigator<RootStackParamList>();

const RootNavigator: React.FC = () => {
  const { isDark } = useTheme();
  const { isLocked, unlock } = useLockScreen();

  return (
    <NavigationContainer
      theme={{
        dark: isDark,
        colors: {
          primary: '#FF5C5C',
          background: isDark ? '#121212' : '#F9F9F9',
          card: isDark ? '#1E1E1E' : '#FFFFFF',
          text: isDark ? '#FFFFFF' : '#121212',
          border: isDark ? '#2C2C2C' : '#E0E0E0',
          notification: '#FF5C5C',
        },
      }}
    >
      <Stack.Navigator
        screenOptions={{
          headerShown: false,
          cardStyle: { backgroundColor: isDark ? '#121212' : '#F9F9F9' },
        }}
      >
        <Stack.Screen name="Main" component={MainTabNavigator} />
        <Stack.Screen 
          name="Settings" 
          component={SettingsScreen}
          options={{
            headerShown: true,
            title: 'Configurações',
            headerTintColor: isDark ? '#FFFFFF' : '#121212',
            headerStyle: {
              backgroundColor: isDark ? '#1E1E1E' : '#FFFFFF',
              elevation: 0,
              shadowOpacity: 0,
            },
          }} 
        />
        <Stack.Screen 
          name="AppSettings" 
          component={AppSettingsScreen}
          options={{
            headerShown: true,
            title: 'Configurações de Apps',
            headerTintColor: isDark ? '#FFFFFF' : '#121212',
            headerStyle: {
              backgroundColor: isDark ? '#1E1E1E' : '#FFFFFF',
              elevation: 0,
              shadowOpacity: 0,
            },
          }} 
        />
        <Stack.Screen 
          name="About" 
          component={AboutScreen}
          options={{
            headerShown: true,
            title: 'Sobre',
            headerTintColor: isDark ? '#FFFFFF' : '#121212',
            headerStyle: {
              backgroundColor: isDark ? '#1E1E1E' : '#FFFFFF',
              elevation: 0,
              shadowOpacity: 0,
            },
          }} 
        />
      </Stack.Navigator>
      
      <LockScreen
        visible={isLocked}
        onUnlock={unlock}
      />
    </NavigationContainer>
  );
};

export default RootNavigator;
EOF