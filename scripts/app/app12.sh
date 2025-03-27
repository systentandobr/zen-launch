#!/bin/bash

# Script 12 criado automaticamente
echo "Executando claude-ai12.sh"
cat << 'EOF' > src/modules/launcher/screens/AppDrawerScreen.tsx
import React, { useState, useEffect } from 'react';
import { View, SafeAreaView, TextInput, StatusBar } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import AppGrid, { AppInfo } from '../components/AppGrid';
import Text from '@core/ui/components/Text';

// Mock data para demonstração
const mockApps: AppInfo[] = [
  { id: '1', name: 'Chrome', packageName: 'com.android.chrome', icon: null },
  { id: '2', name: 'Gmail', packageName: 'com.google.android.gm', icon: null },
  { id: '3', name: 'Maps', packageName: 'com.google.android.maps', icon: null },
  { id: '4', name: 'YouTube', packageName: 'com.google.android.youtube', icon: null },
  { id: '5', name: 'Calendar', packageName: 'com.google.android.calendar', icon: null },
  { id: '6', name: 'Photos', packageName: 'com.google.android.photos', icon: null },
  { id: '7', name: 'Drive', packageName: 'com.google.android.drive', icon: null },
  { id: '8', name: 'Keep', packageName: 'com.google.android.keep', icon: null },
  { id: '9', name: 'Docs', packageName: 'com.google.android.docs', icon: null },
  { id: '10', name: 'Whatsapp', packageName: 'com.whatsapp', icon: null },
  { id: '11', name: 'Instagram', packageName: 'com.instagram.android', icon: null, isRestricted: true },
  { id: '12', name: 'Twitter', packageName: 'com.twitter.android', icon: null, isRestricted: true },
];

const AppDrawerScreen: React.FC = () => {
  const { isDark } = useTheme();
  const [apps, setApps] = useState<AppInfo[]>(mockApps);
  const [searchText, setSearchText] = useState<string>('');
  const [filteredApps, setFilteredApps] = useState<AppInfo[]>(mockApps);

  useEffect(() => {
    if (!searchText) {
      setFilteredApps(apps);
      return;
    }
    
    const filtered = apps.filter(app => 
      app.name.toLowerCase().includes(searchText.toLowerCase())
    );
    setFilteredApps(filtered);
  }, [searchText, apps]);

  const handleAppPress = (app: AppInfo) => {
    // Aqui seria a lógica para abrir o aplicativo
    console.log(`Opening app: ${app.name}`);
  };

  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <View className="px-4 pt-6 pb-2">
        <TextInput
          className={`px-4 py-2 rounded-full mb-4 ${
            isDark 
              ? 'bg-gray-800 text-white' 
              : 'bg-gray-200 text-black'
          }`}
          placeholder="Pesquisar apps..."
          placeholderTextColor={isDark ? '#888' : '#666'}
          value={searchText}
          onChangeText={setSearchText}
        />
        
        {filteredApps.length === 0 ? (
          <View className="items-center justify-center mt-10">
            <Text className="text-center opacity-50">
              Nenhum aplicativo encontrado
            </Text>
          </View>
        ) : (
          <AppGrid 
            apps={filteredApps} 
            onAppPress={handleAppPress}
            columns={4}
          />
        )}
      </View>
    </SafeAreaView>
  );
};

export default AppDrawerScreen;
EOF