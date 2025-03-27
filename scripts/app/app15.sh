#!/bin/bash

# Script 15 criado automaticamente
echo "Executando claude-ai15.sh"
cat << 'EOF' > src/navigation/RootNavigator.tsx
import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import { RootStackParamList } from './types';
import MainTabNavigator from './MainTabNavigator';
import SettingsScreen from '@modules/launcher/screens/SettingsScreen';

const Stack = createStackNavigator<RootStackParamList>();

const RootNavigator: React.FC = () => {
  const { isDark } = useTheme();

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
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default RootNavigator;
EOF

cat << 'EOF' > src/navigation/MainTabNavigator.tsx
import React from 'react';
import { View, TouchableOpacity } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { MainTabParamList } from './types';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';

// Screens
import LauncherHomeScreen from '@modules/launcher/screens/LauncherHomeScreen';
import AppDrawerScreen from '@modules/launcher/screens/AppDrawerScreen';
import FocusModeScreen from '@modules/focus/screens/FocusModeScreen';

// Icons (substitua por seus componentes de ícones reais)
const HomeIcon = ({ focused }: { focused: boolean }) => (
  <View style={{ width: 24, height: 24, backgroundColor: focused ? '#FF5C5C' : 'transparent', borderRadius: 12 }} />
);

const AppsIcon = ({ focused }: { focused: boolean }) => (
  <View style={{ width: 24, height: 24, backgroundColor: focused ? '#FF5C5C' : 'transparent', borderRadius: 4 }} />
);

const FocusIcon = ({ focused }: { focused: boolean }) => (
  <View style={{ width: 24, height: 24, backgroundColor: focused ? '#FF5C5C' : 'transparent', borderRadius: 24 }} />
);

const Tab = createBottomTabNavigator<MainTabParamList>();

const MainTabNavigator: React.FC = () => {
  const { isDark } = useTheme();

  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: '#FF5C5C',
        tabBarInactiveTintColor: isDark ? '#888888' : '#666666',
        tabBarShowLabel: true,
        tabBarStyle: {
          backgroundColor: isDark ? '#1E1E1E' : '#FFFFFF',
          borderTopWidth: 0,
          elevation: 0,
          height: 60,
          paddingBottom: 8,
          paddingTop: 8,
        },
      }}
    >
      <Tab.Screen
        name="Home"
        component={LauncherHomeScreen}
        options={{
          tabBarLabel: 'Início',
          tabBarIcon: ({ focused }) => <HomeIcon focused={focused} />,
        }}
      />
      <Tab.Screen
        name="Apps"
        component={AppDrawerScreen}
        options={{
          tabBarLabel: 'Apps',
          tabBarIcon: ({ focused }) => <AppsIcon focused={focused} />,
        }}
      />
      <Tab.Screen
        name="Focus"
        component={FocusModeScreen}
        options={{
          tabBarLabel: 'Foco',
          tabBarIcon: ({ focused }) => <FocusIcon focused={focused} />,
        }}
      />
    </Tab.Navigator>
  );
};

export default MainTabNavigator;
EOF