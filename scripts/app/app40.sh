#!/bin/bash

# Script 40 criado automaticamente
echo "Executando claude-ai40.sh"
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
import TasksScreen from '@modules/tasks/screens/TasksScreen';
import SettingsScreen from '@modules/launcher/screens/SettingsScreen';

// Icons (substitua por seus componentes de ícones reais)
const HomeIcon = ({ focused }: { focused: boolean }) => (
  <View 
    className={`w-6 h-6 rounded-full items-center justify-center ${
      focused ? 'bg-primary' : 'bg-transparent'
    }`}
  >
    <Text className={`text-lg ${focused ? 'text-white' : ''}`}>🏠</Text>
  </View>
);

const AppsIcon = ({ focused }: { focused: boolean }) => (
  <View 
    className={`w-6 h-6 rounded-sm items-center justify-center ${
      focused ? 'bg-primary' : 'bg-transparent'
    }`}
  >
    <Text className={`text-lg ${focused ? 'text-white' : ''}`}>📱</Text>
  </View>
);

const FocusIcon = ({ focused }: { focused: boolean }) => (
  <View 
    className={`w-6 h-6 rounded-full items-center justify-center ${
      focused ? 'bg-primary' : 'bg-transparent'
    }`}
  >
    <Text className={`text-lg ${focused ? 'text-white' : ''}`}>⏱️</Text>
  </View>
);

const TasksIcon = ({ focused }: { focused: boolean }) => (
  <View 
    className={`w-6 h-6 rounded-sm items-center justify-center ${
      focused ? 'bg-primary' : 'bg-transparent'
    }`}
  >
    <Text className={`text-lg ${focused ? 'text-white' : ''}`}>📋</Text>
  </View>
);

const SettingsIcon = ({ focused }: { focused: boolean }) => (
  <View 
    className={`w-6 h-6 rounded-full items-center justify-center ${
      focused ? 'bg-primary' : 'bg-transparent'
    }`}
  >
    <Text className={`text-lg ${focused ? 'text-white' : ''}`}>⚙️</Text>
  </View>
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
      <Tab.Screen
        name="Tasks"
        component={TasksScreen}
        options={{
          tabBarLabel: 'Tarefas',
          tabBarIcon: ({ focused }) => <TasksIcon focused={focused} />,
        }}
      />
      <Tab.Screen
        name="Settings"
        component={SettingsScreen}
        options={{
          tabBarLabel: 'Ajustes',
          tabBarIcon: ({ focused }) => <SettingsIcon focused={focused} />,
        }}
      />
    </Tab.Navigator>
  );
};

export default MainTabNavigator;
EOF