#!/bin/bash

# Script 32 criado automaticamente
echo "Executando claude-ai32.sh"
cat << 'EOF' > src/modules/focus/components/SelectAppsModal.tsx
import React, { useState, useEffect } from 'react';
import { 
  View, 
  Modal, 
  TouchableOpacity, 
  TouchableWithoutFeedback, 
  FlatList, 
  TextInput 
} from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import { AppInfo } from '@state/slices/appsSlice';

interface SelectAppsModalProps {
  visible: boolean;
  onClose: () => void;
  apps: AppInfo[];
  onSelectApp: (packageName: string) => void;
  restrictedApps: string[];
}

const SelectAppsModal: React.FC<SelectAppsModalProps> = ({
  visible,
  onClose,
  apps,
  onSelectApp,
  restrictedApps
}) => {
  const { isDark } = useTheme();
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredApps, setFilteredApps] = useState<AppInfo[]>([]);

  useEffect(() => {
    // Filtra apps que não estão na lista de restritos e correspondem à busca
    setFilteredApps(
      apps.filter(app => 
        !restrictedApps.includes(app.packageName) && 
        app.name.toLowerCase().includes(searchQuery.toLowerCase())
      )
    );
  }, [apps, restrictedApps, searchQuery]);

  const handleBackdropPress = () => {
    onClose();
  };

  const handleModalContentPress = (e: any) => {
    e.stopPropagation();
  };

  const renderApp = ({ item }: { item: AppInfo }) => (
    <TouchableOpacity
      className={`py-3 px-4 border-b ${isDark ? 'border-gray-800' : 'border-gray-200'}`}
      onPress={() => {
        onSelectApp(item.packageName);
        onClose();
      }}
    >
      <Text>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={handleBackdropPress}>
        <View className="flex-1 justify-center items-center" style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
          <TouchableWithoutFeedback onPress={handleModalContentPress}>
            <View 
              className={`w-11/12 h-3/4 rounded-xl ${
                isDark ? 'bg-card-dark' : 'bg-card-light'
              }`}
            >
              <View className="p-4 border-b border-gray-300 flex-row justify-between items-center">
                <Text variant="h3">Selecionar App</Text>
                <TouchableOpacity onPress={onClose}>
                  <Text className="text-xl">✕</Text>
                </TouchableOpacity>
              </View>
              
              <TextInput
                className={`mx-4 my-3 p-3 rounded-lg ${
                  isDark 
                    ? 'bg-gray-800 text-white' 
                    : 'bg-gray-100 text-black'
                }`}
                placeholder="Pesquisar apps..."
                placeholderTextColor={isDark ? '#888' : '#666'}
                value={searchQuery}
                onChangeText={setSearchQuery}
              />
              
              {filteredApps.length > 0 ? (
                <FlatList
                  data={filteredApps}
                  renderItem={renderApp}
                  keyExtractor={item => item.packageName}
                  className="px-4"
                />
              ) : (
                <View className="flex-1 justify-center items-center">
                  <Text className="opacity-50 italic">
                    {searchQuery 
                      ? 'Nenhum app encontrado' 
                      : 'Todos os apps já estão restritos'}
                  </Text>
                </View>
              )}
              
              <View className="p-4">
                <Button variant="outline" onPress={onClose}>
                  Cancelar
                </Button>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
    </Modal>
  );
};

export default SelectAppsModal;
EOF