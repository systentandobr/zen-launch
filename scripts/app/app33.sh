#!/bin/bash

# Script 33 criado automaticamente
echo "Executando claude-ai33.sh"
cat << 'EOF' > src/modules/focus/screens/FocusModeScreen.tsx
import React, { useState, useEffect } from 'react';
import { View, ScrollView, SafeAreaView, StatusBar, Alert } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import FocusMode from '../components/FocusMode';
import AppUsageChart from '../components/AppUsageChart';
import RestrictedApps from '../components/RestrictedApps';
import SelectAppsModal from '../components/SelectAppsModal';
import useFocusMode from '../hooks/useFocusMode';
import { useApps } from '@modules/launcher/hooks/useApps';

const FocusModeScreen: React.FC = () => {
  const { isDark } = useTheme();
  const { 
    isActive, 
    timeRemaining,
    restrictedApps, 
    appUsage, 
    startFocus, 
    stopFocus, 
    addRestricted, 
    removeRestricted,
    checkPermissions,
    requestPermissions
  } = useFocusMode();
  
  const { apps } = useApps();
  const [selectAppsModalVisible, setSelectAppsModalVisible] = useState(false);
  const [hasPermission, setHasPermission] = useState<boolean>(false);

  useEffect(() => {
    const verifyPermissions = async () => {
      const hasUsageStatsPermission = await checkPermissions();
      setHasPermission(hasUsageStatsPermission);
      
      if (!hasUsageStatsPermission) {
        Alert.alert(
          "Permissão Necessária",
          "Para usar o modo foco e visualizar estatísticas de uso, permita o acesso às estatísticas de uso.",
          [
            { text: "Cancelar", style: "cancel" },
            { text: "Permitir", onPress: requestPermissions }
          ]
        );
      }
    };

    verifyPermissions();
  }, []);

  const handleToggleFocusMode = (active: boolean) => {
    if (active) {
      return; // Isso será tratado no método handleSetFocusDuration
    } else {
      stopFocus();
    }
  };

  const handleSetFocusDuration = (minutes: number) => {
    if (!hasPermission) {
      Alert.alert(
        "Permissão Necessária",
        "Para usar o modo foco, permita o acesso às estatísticas de uso.",
        [
          { text: "Cancelar", style: "cancel" },
          { text: "Permitir", onPress: requestPermissions }
        ]
      );
      return;
    }
    
    startFocus(minutes);
  };

  const handleAddRestrictedApp = () => {
    setSelectAppsModalVisible(true);
  };

  const renderUsageStats = () => {
    if (!hasPermission) {
      return (
        <View className="items-center py-8 px-4">
          <Text className="text-center mb-4">
            Para visualizar estatísticas de uso, é necessário conceder permissão de acesso.
          </Text>
          <Button onPress={requestPermissions}>Conceder Permissão</Button>
        </View>
      );
    }

    if (appUsage.length === 0) {
      return (
        <View className="items-center py-8">
          <Text className="opacity-50 italic">
            Nenhum dado de uso disponível
          </Text>
        </View>
      );
    }

    return (
      <>
        <AppUsageChart 
          data={appUsage}
          title="Uso Diário"
          period="day"
          className="mb-4"
        />
        
        <AppUsageChart 
          data={appUsage.map(app => ({ ...app, usageTime: app.usageTime * 7 }))}
          title="Uso Semanal"
          period="week"
          className="mb-4"
        />
      </>
    );
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
          isActive={isActive}
          onToggle={handleToggleFocusMode}
          onSetDuration={handleSetFocusDuration}
          timeRemaining={timeRemaining}
          className="mb-4"
        />
        
        <RestrictedApps
          restrictedApps={restrictedApps}
          apps={apps}
          onRemoveApp={removeRestricted}
          onAddApp={handleAddRestrictedApp}
          className="mb-4"
        />
        
        {renderUsageStats()}
      </ScrollView>
      
      <SelectAppsModal
        visible={selectAppsModalVisible}
        onClose={() => setSelectAppsModalVisible(false)}
        apps={apps}
        onSelectApp={addRestricted}
        restrictedApps={restrictedApps}
      />
    </SafeAreaView>
  );
};

export default FocusModeScreen;
EOF