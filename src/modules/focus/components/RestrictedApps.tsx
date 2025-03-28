import React from 'react';
import { View, FlatList } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import { useTheme } from '@core/ui/theme/ThemeContext';
import { AppInfo } from '@state/slices/appsSlice';

interface RestrictedAppsProps {
  restrictedApps: string[];
  apps: AppInfo[];
  onRemoveApp: (packageName: string) => void;
  onAddApp: () => void;
  className?: string;
}

const RestrictedApps: React.FC<RestrictedAppsProps> = ({
  restrictedApps,
  apps,
  onRemoveApp,
  onAddApp,
  className = '',
}) => {
  const { isDark } = useTheme();

  // Filtra os apps com base nos packageNames restritos
  const restrictedAppsList = apps.filter(app => 
    restrictedApps.includes(app.packageName)
  );

  const renderApp = ({ item }: { item: AppInfo }) => (
    <View 
      className={`flex-row justify-between items-center py-3 px-2 mb-2 rounded-lg ${
        isDark ? 'bg-gray-800' : 'bg-gray-100'
      }`}
    >
      <Text>{item.name}</Text>
      <Button 
        variant="ghost" 
        size="sm"
        onPress={() => onRemoveApp(item.packageName)}
        className="p-1"
      >
        <Text className="text-primary">Remover</Text>
      </Button>
    </View>
  );

  return (
    <Card className={className}>
      <Text variant="h3" className="mb-4">Apps Restritos</Text>
      
      {restrictedAppsList.length > 0 ? (
        <FlatList
          data={restrictedAppsList}
          renderItem={renderApp}
          keyExtractor={item => item.packageName}
          scrollEnabled={false}
          className="mb-4"
        />
      ) : (
        <Text className="text-center py-4 italic opacity-50 mb-4">
          Nenhum app restrito
        </Text>
      )}
      
      <Button 
        variant="outline" 
        onPress={onAddApp}
      >
        Adicionar app restrito
      </Button>
    </Card>
  );
};

export default RestrictedApps;
