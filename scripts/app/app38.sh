#!/bin/bash

# Script 38 criado automaticamente
echo "Executando claude-ai38.sh"
cat << 'EOF' > src/modules/launcher/components/EnhancedAppGrid.tsx
import React, { useState } from 'react';
import { View, FlatList, Dimensions, TouchableOpacity, StyleSheet } from 'react-native';
import AppIcon from '@core/ui/components/AppIcon';
import { AppInfo } from '@state/slices/appsSlice';
import AppCategoryModal from './AppCategoryModal';
import Text from '@core/ui/components/Text';
import { useTheme } from '@core/ui/theme/ThemeContext';

interface EnhancedAppGridProps {
  apps: AppInfo[];
  onAppPress: (app: AppInfo) => void;
  columns?: number;
  onLongPress?: (app: AppInfo) => void;
  getAppCategory?: (packageName: string) => string;
  onCategoryChange?: (packageName: string, category: string) => void;
  categories?: string[];
  editMode?: boolean;
  className?: string;
}

const EnhancedAppGrid: React.FC<EnhancedAppGridProps> = ({
  apps,
  onAppPress,
  columns = 4,
  onLongPress,
  getAppCategory,
  onCategoryChange,
  categories = [],
  editMode = false,
  className = '',
}) => {
  const { isDark } = useTheme();
  const [selectedApp, setSelectedApp] = useState<AppInfo | null>(null);
  const [categoryModalVisible, setCategoryModalVisible] = useState<boolean>(false);
  
  // Ajusta automaticamente o número de colunas baseado na largura da tela
  const screenWidth = Dimensions.get('window').width;
  const isTablet = screenWidth >= 768;
  const responsiveColumns = isTablet ? columns + 2 : columns;
  
  const handleAppLongPress = (app: AppInfo) => {
    if (!editMode) return;
    
    setSelectedApp(app);
    
    if (onLongPress) {
      onLongPress(app);
    }
    
    if (getAppCategory && onCategoryChange) {
      setCategoryModalVisible(true);
    }
  };
  
  const handleCategorySelect = (category: string) => {
    if (selectedApp && onCategoryChange) {
      onCategoryChange(selectedApp.packageName, category);
    }
    setCategoryModalVisible(false);
  };
  
  const renderApp = ({ item }: { item: AppInfo }) => (
    <View className="p-2 items-center">
      <TouchableOpacity
        activeOpacity={0.7}
        onPress={() => onAppPress(item)}
        onLongPress={() => handleAppLongPress(item)}
        delayLongPress={500} // Meio segundo para ativar o longpress
        className="items-center"
      >
        <AppIcon
          name={item.name}
          icon={item.icon}
          isRestricted={item.isRestricted}
          size="md"
        />
        
        {editMode && getAppCategory && (
          <View className={`mt-1 py-0.5 px-2 rounded-full ${
            isDark ? 'bg-gray-800' : 'bg-gray-200'
          }`}>
            <Text variant="caption" className="text-xs">
              {getAppCategory(item.packageName)}
            </Text>
          </View>
        )}
      </TouchableOpacity>
    </View>
  );

  return (
    <View className={`flex-1 ${className}`}>
      <FlatList
        data={apps}
        renderItem={renderApp}
        keyExtractor={item => item.id}
        numColumns={responsiveColumns}
        columnWrapperStyle={{ justifyContent: 'space-around' }}
        showsVerticalScrollIndicator={false}
        contentContainerStyle={{ paddingBottom: 80 }}
      />
      
      {selectedApp && (
        <AppCategoryModal
          visible={categoryModalVisible}
          onClose={() => setCategoryModalVisible(false)}
          selectedCategory={getAppCategory ? getAppCategory(selectedApp.packageName) : ''}
          categories={categories}
          onSelectCategory={handleCategorySelect}
          appName={selectedApp.name}
        />
      )}
    </View>
  );
};

export default EnhancedAppGrid;
EOF