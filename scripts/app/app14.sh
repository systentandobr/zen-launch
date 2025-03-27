#!/bin/bash

# Script 14 criado automaticamente
echo "Executando claude-ai14.sh"
cat << 'EOF' > src/modules/focus/screens/FocusModeScreen.tsx
import React, { useState, useEffect } from 'react';
import { View, ScrollView, SafeAreaView, StatusBar } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Card from '@core/ui/components/Card';
import Button from '@core/ui/components/Button';
import FocusMode from '../components/FocusMode';
import AppUsageChart, { AppUsage } from '../components/AppUsageChart';

// Mock data para demonstração
const mockAppUsage: AppUsage[] = [
  { appName: 'Chrome', packageName: 'com.android.chrome', usageTime: 120 },
  { appName: 'Instagram', packageName: 'com.instagram.android', usageTime: 65 },
  { appName: 'WhatsApp', packageName: 'com.whatsapp', usageTime: 45 },
  { appName: 'Gmail', packageName: 'com.google.android.gm', usageTime: 30 },
  { appName: 'YouTube', packageName: 'com.google.android.youtube', usageTime: 25 },
];

const mockRestrictedApps = [
  { name: 'Instagram', packageName: 'com.instagram.android' },
  { name: 'Twitter', packageName: 'com.twitter.android' },
  { name: 'Facebook', packageName: 'com.facebook.katana' },
  { name: 'TikTok', packageName: 'com.zhiliaoapp.musically' },
];

const FocusModeScreen: React.FC = () => {
  const { isDark } = useTheme();
  const [focusMode, setFocusMode] = useState<boolean>(false);
  const [focusTimeRemaining, setFocusTimeRemaining] = useState<number | undefined>(undefined);
  const [restrictedApps, setRestrictedApps] = useState<{ name: string, packageName: string }[]>(mockRestrictedApps);

  // Efeito para atualizar o timer quando o modo foco estiver ativo
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (focusMode && focusTimeRemaining) {
      interval = setInterval(() => {
        setFocusTimeRemaining(prev => {
          if (prev && prev > 0) {
            return prev - 1;
          } else {
            setFocusMode(false);
            clearInterval(interval);
            return undefined;
          }
        });
      }, 1000);
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [focusMode, focusTimeRemaining]);

  const handleToggleFocusMode = (active: boolean) => {
    setFocusMode(active);
    if (!active) {
      setFocusTimeRemaining(undefined);
    }
  };

  const handleSetFocusDuration = (minutes: number) => {
    setFocusTimeRemaining(minutes * 60);
    setFocusMode(true);
  };

  const handleRemoveRestrictedApp = (packageName: string) => {
    setRestrictedApps(prev => prev.filter(app => app.packageName !== packageName));
  };

  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <ScrollView className="flex-1 px-4 pt-6">
        <Text variant="h2" className="mb-6">Modo Foco</Text>
        
        <FocusMode 
          isActive={focusMode}
          onToggle={handleToggleFocusMode}
          onSetDuration={handleSetFocusDuration}
          timeRemaining={focusTimeRemaining}
          className="mb-4"
        />
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-4">Apps Restritos</Text>
          
          {restrictedApps.length > 0 ? (
            restrictedApps.map(app => (
              <View 
                key={app.packageName}
                className={`flex-row justify-between items-center py-3 px-2 mb-2 rounded-lg ${
                  isDark ? 'bg-gray-800' : 'bg-gray-100'
                }`}
              >
                <Text>{app.name}</Text>
                <Button 
                  variant="ghost" 
                  size="sm"
                  onPress={() => handleRemoveRestrictedApp(app.packageName)}
                  className="p-1"
                >
                  <Text className="text-primary">Remover</Text>
                </Button>
              </View>
            ))
          ) : (
            <Text className="text-center py-4 italic opacity-50">
              Nenhum app restrito
            </Text>
          )}
          
          <Button 
            variant="outline" 
            className="mt-2"
            onPress={() => {/* Lógica para adicionar app restrito */}}
          >
            Adicionar app restrito
          </Button>
        </Card>
        
        <AppUsageChart 
          data={mockAppUsage}
          title="Uso Diário"
          period="day"
          className="mb-4"
        />
        
        <AppUsageChart 
          data={[...mockAppUsage].map(app => ({ ...app, usageTime: app.usageTime * 7 }))}
          title="Uso Semanal"
          period="week"
          className="mb-4"
        />
      </ScrollView>
    </SafeAreaView>
  );
};

export default FocusModeScreen;
EOF