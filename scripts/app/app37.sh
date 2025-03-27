#!/bin/bash

# Script 37 criado automaticamente
echo "Executando claude-ai37.sh"
cat << 'EOF' > src/modules/launcher/components/AppCategoryModal.tsx
import React from 'react';
import { 
  View, 
  Modal, 
  TouchableOpacity, 
  TouchableWithoutFeedback, 
  FlatList 
} from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';

interface AppCategoryModalProps {
  visible: boolean;
  onClose: () => void;
  selectedCategory: string;
  categories: string[];
  onSelectCategory: (category: string) => void;
  appName: string;
}

const AppCategoryModal: React.FC<AppCategoryModalProps> = ({
  visible,
  onClose,
  selectedCategory,
  categories,
  onSelectCategory,
  appName
}) => {
  const { isDark } = useTheme();

  const handleBackdropPress = () => {
    onClose();
  };

  const handleModalContentPress = (e: any) => {
    e.stopPropagation();
  };

  const renderCategory = ({ item }: { item: string }) => (
    <TouchableOpacity
      className={`py-3 px-4 my-1 rounded-lg ${
        selectedCategory === item 
          ? 'bg-primary' 
          : isDark ? 'bg-gray-800' : 'bg-gray-100'
      }`}
      onPress={() => onSelectCategory(item)}
    >
      <Text className={selectedCategory === item ? 'text-white font-medium' : ''}>
        {item}
      </Text>
    </TouchableOpacity>
  );

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={handleBackdropPress}>
        <View className="flex-1 justify-center items-center" style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
          <TouchableWithoutFeedback onPress={handleModalContentPress}>
            <View 
              className={`w-4/5 rounded-xl p-4 ${
                isDark ? 'bg-card-dark' : 'bg-card-light'
              }`}
            >
              <Text variant="h3" className="mb-2">Selecionar Categoria</Text>
              <Text className="mb-4">
                Selecione uma categoria para "{appName}"
              </Text>
              
              <FlatList
                data={categories}
                renderItem={renderCategory}
                keyExtractor={item => item}
                className="max-h-80"
              />
              
              <View className="flex-row justify-end mt-4">
                <Button variant="ghost" onPress={onClose} className="mr-2">
                  Cancelar
                </Button>
                <Button onPress={onClose}>
                  OK
                </Button>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
    </Modal>
  );
};

export default AppCategoryModal;
EOF