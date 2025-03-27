#!/bin/bash

# Script 7 criado automaticamente
echo "Executando claude-ai7.sh"
cat << 'EOF' > src/modules/launcher/components/AppGrid.tsx
import React from 'react';
import { View, FlatList, Dimensions } from 'react-native';
import AppIcon from '@core/ui/components/AppIcon';

export interface AppInfo {
  id: string;
  name: string;
  packageName: string;
  icon: any;
  isRestricted?: boolean;
  category?: string;
}

interface AppGridProps {
  apps: AppInfo[];
  onAppPress: (app: AppInfo) => void;
  columns?: number;
  className?: string;
}

const AppGrid: React.FC<AppGridProps> = ({
  apps,
  onAppPress,
  columns = 4,
  className = '',
}) => {
  // Ajusta automaticamente o número de colunas baseado na largura da tela
  const screenWidth = Dimensions.get('window').width;
  const isTablet = screenWidth >= 768;
  const responsiveColumns = isTablet ? columns + 2 : columns;
  
  const renderApp = ({ item }: { item: AppInfo }) => (
    <View className="p-2 items-center">
      <AppIcon
        name={item.name}
        icon={item.icon}
        isRestricted={item.isRestricted}
        onPress={() => onAppPress(item)}
      />
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
    </View>
  );
};

export default AppGrid;
EOF