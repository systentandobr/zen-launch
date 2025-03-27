#!/bin/bash

# Script 39 criado automaticamente
echo "Executando claude-ai39.sh"
cat << 'EOF' > src/modules/launcher/screens/AppSettingsScreen.tsx
import React, { useState } from 'react';
import { View, ScrollView, SafeAreaView, StatusBar, Alert, Switch, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import Card from '@core/ui/components/Card';
import { useApps } from '../hooks/useApps';
import useAppCategories from '../hooks/useAppCategories';
import EnhancedAppGrid from '../components/EnhancedAppGrid';
import useSettings from '../hooks/useSettings';
import SystemSettingsModule from '@core/infrastructure/native/SystemSettingsModule';

const AppSettingsScreen: React.FC = () => {
  const { isDark } = useTheme();
  const navigation = useNavigation();
  const { apps, isLoading } = useApps();
  const { 
    categories, 
    getAppCategory, 
    setAppCategory, 
    resetCategories 
  } = useAppCategories(apps);
  const { settings, updateAppOrientation, restoreDefaults } = useSettings();
  
  const [editMode, setEditMode] = useState<boolean>(false);
  
  const handleAppPress = async (app: any) => {
    if (editMode) {
      // No modo de edição, abre as configurações do app
      await SystemSettingsModule.openAppInfo(app.packageName);
    }
  };
  
  const handleResetCategories = () => {
    Alert.alert(
      "Redefinir Categorias",
      "Tem certeza de que deseja redefinir todas as categorias de apps para os valores padrão?",
      [
        { text: "Cancelar", style: "cancel" },
        { 
          text: "Redefinir", 
          onPress: async () => {
            const success = await resetCategories();
            if (success) {
              Alert.alert(
                "Categorias Redefinidas",
                "As categorias de aplicativos foram restauradas para os valores padrão."
              );
            }
          },
          style: "destructive"
        }
      ]
    );
  };
  
  const handleResetAllSettings = () => {
    Alert.alert(
      "Redefinir Configurações",
      "Tem certeza de que deseja redefinir todas as configurações do launcher para os valores padrão?",
      [
        { text: "Cancelar", style: "cancel" },
        { 
          text: "Redefinir", 
          onPress: () => {
            restoreDefaults();
            resetCategories();
            Alert.alert(
              "Configurações Redefinidas",
              "Todas as configurações foram restauradas para os valores padrão."
            );
          },
          style: "destructive"
        }
      ]
    );
  };
  
  const renderOrientationSelector = () => (
    <View className={`py-3 px-1 border-b ${isDark ? 'border-gray-800' : 'border-gray-200'}`}>
      <Text className="mb-2">Orientação do Aplicativo</Text>
      
      <View className="flex-row justify-between mt-2">
        {['portrait', 'landscape', 'auto'].map((orientation) => (
          <TouchableOpacity
            key={orientation}
            className={`py-2 px-4 rounded-lg ${
              settings.appOrientation === orientation 
                ? 'bg-primary' 
                : isDark ? 'bg-gray-800' : 'bg-gray-200'
            }`}
            onPress={() => updateAppOrientation(orientation as 'portrait' | 'landscape' | 'auto')}
          >
            <Text className={settings.appOrientation === orientation ? 'text-white' : ''}>
              {orientation === 'portrait' ? 'Retrato' : 
               orientation === 'landscape' ? 'Paisagem' : 'Automático'}
            </Text>
          </TouchableOpacity>
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
        <View className="flex-row justify-between items-center mb-6">
          <Text variant="h2">Configurações de Apps</Text>
          
          <Switch
            value={editMode}
            onValueChange={setEditMode}
            trackColor={{ false: '#767577', true: '#C1C1C1' }}
            thumbColor={editMode ? '#FF5C5C' : '#f4f3f4'}
          />
        </View>
        
        <Card className="mb-4">
          <View className="flex-row justify-between items-center mb-4">
            <Text variant="h3">Organização de Apps</Text>
            <Text variant="caption" className={editMode ? 'text-primary' : 'opacity-50'}>
              {editMode ? 'Modo de edição ativo' : 'Modo de visualização'}
            </Text>
          </View>
          
          <Text className="mb-4">
            {editMode 
              ? 'Toque e segure um app para mudar sua categoria ou toque para acessar suas configurações.'
              : 'Ative o modo de edição para gerenciar os apps e suas categorias.'}
          </Text>
          
          <View className="h-60">
            <EnhancedAppGrid
              apps={apps.slice(0, 8)} // Limita para mostrar apenas alguns apps
              onAppPress={handleAppPress}
              columns={4}
              getAppCategory={getAppCategory}
              onCategoryChange={setAppCategory}
              categories={categories}
              editMode={editMode}
            />
          </View>
          
          <Button
            variant="outline"
            onPress={handleResetCategories}
            className="mt-2"
          >
            Redefinir Categorias
          </Button>
        </Card>
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-4">Layout</Text>
          
          {renderOrientationSelector()}
        </Card>
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-4">Redefinir</Text>
          
          <Text className="mb-4">
            Redefinir todas as configurações do launcher para os valores padrão.
            Esta ação não afetará seus aplicativos ou dados.
          </Text>
          
          <Button
            variant="outline"
            onPress={handleResetAllSettings}
            className="bg-red-500 bg-opacity-10"
          >
            <Text className="text-red-500">Redefinir Todas as Configurações</Text>
          </Button>
        </Card>
      </ScrollView>
    </SafeAreaView>
  );
};

export default AppSettingsScreen;
EOF