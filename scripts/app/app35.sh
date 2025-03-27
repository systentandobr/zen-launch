#!/bin/bash

# Script 35 criado automaticamente
echo "Executando claude-ai35.sh"
cat << 'EOF' > src/modules/launcher/screens/AppDrawerScreen.tsx
import React, { useState, useEffect } from 'react';
import { View, SafeAreaView, StatusBar, TouchableOpacity, ScrollView } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import SearchBar from '@core/ui/components/SearchBar';
import AppGrid from '../components/AppGrid';
import useFocusMode from '@modules/focus/hooks/useFocusMode';
import { useApps } from '../hooks/useApps';

const AppDrawerScreen: React.FC = () => {
  const { isDark } = useTheme();
  const { apps, isLoading, launchApp } = useApps();
  const { isActive, restrictedApps } = useFocusMode();
  
  const [searchText, setSearchText] = useState<string>('');
  const [filteredApps, setFilteredApps] = useState(apps);
  const [activeCategory, setActiveCategory] = useState<string | null>(null);
  
  // Lista de categorias disponíveis
  const categories = [
    'Social',
    'Produtividade',
    'Jogos',
    'Mídia',
    'Utilidades'
  ];

  useEffect(() => {
    filterApps();
  }, [searchText, activeCategory, apps, restrictedApps, isActive]);

  const filterApps = () => {
    let filtered = [...apps];
    
    // Adiciona a flag de restrição aos apps se o modo foco estiver ativo
    if (isActive) {
      filtered = filtered.map(app => ({
        ...app,
        isRestricted: restrictedApps.includes(app.packageName)
      }));
    }
    
    // Filtra por texto de busca
    if (searchText) {
      filtered = filtered.filter(app => 
        app.name.toLowerCase().includes(searchText.toLowerCase())
      );
    }
    
    // Filtra por categoria
    if (activeCategory) {
      filtered = filtered.filter(app => app.category === activeCategory);
    }
    
    setFilteredApps(filtered);
  };

  const handleClearSearch = () => {
    setSearchText('');
  };

  const handleAppPress = async (app: any) => {
    if (app.isRestricted) {
      // Se o app estiver restrito, mostra um alerta ou feedback
      return;
    }
    
    await launchApp(app.packageName);
  };

  const handleCategoryPress = (category: string) => {
    setActiveCategory(activeCategory === category ? null : category);
  };

  const renderCategoryTabs = () => (
    <ScrollView 
      horizontal 
      showsHorizontalScrollIndicator={false}
      className="mb-4"
    >
      <TouchableOpacity
        className={`py-2 px-4 mr-2 rounded-full ${
          activeCategory === null 
            ? 'bg-primary' 
            : isDark ? 'bg-gray-800' : 'bg-gray-200'
        }`}
        onPress={() => setActiveCategory(null)}
      >
        <Text className={activeCategory === null ? 'text-white' : ''}>
          Todos
        </Text>
      </TouchableOpacity>
      
      {categories.map(category => (
        <TouchableOpacity
          key={category}
          className={`py-2 px-4 mr-2 rounded-full ${
            activeCategory === category 
              ? 'bg-primary' 
              : isDark ? 'bg-gray-800' : 'bg-gray-200'
          }`}
          onPress={() => handleCategoryPress(category)}
        >
          <Text className={activeCategory === category ? 'text-white' : ''}>
            {category}
          </Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );

  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <View className="px-4 pt-6 pb-2">
        <SearchBar
          value={searchText}
          onChangeText={setSearchText}
          placeholder="Pesquisar apps..."
          onClear={handleClearSearch}
          className="mb-4"
        />
        
        {renderCategoryTabs()}
        
        {isLoading ? (
          <View className="items-center justify-center flex-1">
            <Text>Carregando apps...</Text>
          </View>
        ) : filteredApps.length === 0 ? (
          <View className="items-center justify-center py-10">
            <Text className="text-center opacity-50">
              Nenhum app encontrado
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