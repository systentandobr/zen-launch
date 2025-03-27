#!/bin/bash

# Script 13 criado automaticamente
echo "Executando claude-ai13.sh"
cat << 'EOF' > src/modules/launcher/screens/SettingsScreen.tsx
import React, { useState } from 'react';
import { View, ScrollView, Switch, Pressable, SafeAreaView, StatusBar } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Card from '@core/ui/components/Card';
import Button from '@core/ui/components/Button';

const SettingsScreen: React.FC = () => {
  const { isDark, themeMode, setThemeMode } = useTheme();
  
  const [showClock, setShowClock] = useState<boolean>(true);
  const [showWeather, setShowWeather] = useState<boolean>(true);
  const [enableFocusMode, setEnableFocusMode] = useState<boolean>(true);
  const [is24HourFormat, setIs24HourFormat] = useState<boolean>(true);
  const [showSeconds, setShowSeconds] = useState<boolean>(false);
  
  const renderSectionTitle = (title: string) => (
    <Text variant="h3" className="mb-2 mt-4 px-1">{title}</Text>
  );
  
  const renderSettingItem = (
    label: string, 
    value: boolean, 
    onToggle: (value: boolean) => void,
    description?: string
  ) => (
    <View className={`py-3 px-1 border-b ${isDark ? 'border-gray-800' : 'border-gray-200'}`}>
      <View className="flex-row justify-between items-center">
        <Text>{label}</Text>
        <Switch
          value={value}
          onValueChange={onToggle}
          trackColor={{ false: '#767577', true: '#C1C1C1' }}
          thumbColor={value ? '#FF5C5C' : '#f4f3f4'}
        />
      </View>
      {description && (
        <Text variant="caption" className="mt-1 opacity-70">
          {description}
        </Text>
      )}
    </View>
  );
  
  const renderThemeSelector = () => (
    <View className={`py-3 px-1 border-b ${isDark ? 'border-gray-800' : 'border-gray-200'}`}>
      <Text className="mb-2">Tema</Text>
      
      <View className="flex-row justify-between mt-2">
        {['light', 'dark', 'system'].map((theme) => (
          <Pressable
            key={theme}
            className={`py-2 px-4 rounded-lg ${
              themeMode === theme 
                ? 'bg-primary' 
                : isDark ? 'bg-gray-800' : 'bg-gray-200'
            }`}
            onPress={() => setThemeMode(theme as 'light' | 'dark' | 'system')}
          >
            <Text className={themeMode === theme ? 'text-white' : ''}>
              {theme === 'light' ? 'Claro' : theme === 'dark' ? 'Escuro' : 'Sistema'}
            </Text>
          </Pressable>
        ))}
      </View>
    </View>
  );

  const renderAppOrientation = () => (
    <View className={`py-3 px-1 border-b ${isDark ? 'border-gray-800' : 'border-gray-200'}`}>
      <Text className="mb-2">Orientação do aplicativo</Text>
      
      <View className="flex-row justify-between mt-2">
        {['portrait', 'landscape', 'auto'].map((orientation) => (
          <Pressable
            key={orientation}
            className={`py-2 px-4 rounded-lg ${
              orientation === 'portrait' 
                ? 'bg-primary' 
                : isDark ? 'bg-gray-800' : 'bg-gray-200'
            }`}
            onPress={() => {/* Lógica para mudar orientação */}}
          >
            <Text className={orientation === 'portrait' ? 'text-white' : ''}>
              {orientation === 'portrait' ? 'Retrato' : 
               orientation === 'landscape' ? 'Paisagem' : 'Automático'}
            </Text>
          </Pressable>
        ))}
      </View>
    </View>
  );

  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <ScrollView className="flex-1 px-4 pt-6">
        <Text variant="h2" className="mb-6">Configurações</Text>
        
        <Card className="mb-4">
          {renderSectionTitle("Interface")}
          
          {renderThemeSelector()}
          
          {renderAppOrientation()}
          
          {renderSettingItem(
            "Mostrar relógio", 
            showClock, 
            setShowClock, 
            "Exibir o relógio na tela inicial"
          )}
          
          {renderSettingItem(
            "Formato 24 horas", 
            is24HourFormat, 
            setIs24HourFormat
          )}
          
          {renderSettingItem(
            "Mostrar segundos", 
            showSeconds, 
            setShowSeconds
          )}
        </Card>
        
        <Card className="mb-4">
          {renderSectionTitle("Widgets")}
          
          {renderSettingItem(
            "Mostrar clima", 
            showWeather, 
            setShowWeather, 
            "Exibir informações meteorológicas na tela inicial"
          )}
          
          {renderSettingItem(
            "Modo foco", 
            enableFocusMode, 
            setEnableFocusMode, 
            "Permitir ativar o modo de foco para limitar o uso de apps"
          )}
        </Card>
        
        <Card className="mb-4">
          {renderSectionTitle("Sobre")}
          
          <View className="py-3 px-1">
            <Text>Versão do aplicativo: 1.0.0</Text>
            <Text variant="caption" className="mt-1 opacity-70">
              Zen Launcher
            </Text>
          </View>
          
          <Button 
            variant="outline" 
            className="mt-2 mb-2"
            onPress={() => {/* Lógica para redefinir configurações */}}
          >
            Redefinir configurações
          </Button>
        </Card>
      </ScrollView>
    </SafeAreaView>
  );
};

export default SettingsScreen;
EOF