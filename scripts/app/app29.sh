#!/bin/bash

# Script 29 criado automaticamente
echo "Executando claude-ai29.sh"
cat << 'EOF' > src/modules/tasks/components/AddTaskModal.tsx
import React, { useState, useEffect } from 'react';
import { 
  View, 
  Modal, 
  TextInput, 
  TouchableOpacity, 
  TouchableWithoutFeedback, 
  KeyboardAvoidingView, 
  Platform 
} from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';

interface AddTaskModalProps {
  visible: boolean;
  onClose: () => void;
  onAddTask: (title: string, category?: string) => void;
  categories?: string[];
}

const AddTaskModal: React.FC<AddTaskModalProps> = ({
  visible,
  onClose,
  onAddTask,
  categories = []
}) => {
  const { isDark } = useTheme();
  const [taskTitle, setTaskTitle] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!visible) {
      // Reseta o estado quando o modal é fechado
      setTaskTitle('');
      setSelectedCategory(undefined);
    }
  }, [visible]);

  const handleAddTask = () => {
    if (taskTitle.trim() === '') return;
    
    onAddTask(taskTitle.trim(), selectedCategory);
    onClose();
  };

  const handleBackdropPress = () => {
    onClose();
  };

  const handleModalContentPress = (e: any) => {
    e.stopPropagation();
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={handleBackdropPress}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="flex-1 justify-center items-center"
          style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }}
        >
          <TouchableWithoutFeedback onPress={handleModalContentPress}>
            <View 
              className={`w-5/6 p-4 rounded-xl ${
                isDark ? 'bg-card-dark' : 'bg-card-light'
              }`}
            >
              <Text variant="h3" className="mb-4">Adicionar Tarefa</Text>
              
              <TextInput
                className={`p-3 mb-4 rounded-lg ${
                  isDark 
                    ? 'bg-gray-800 text-white' 
                    : 'bg-gray-100 text-black'
                }`}
                placeholder="Título da tarefa"
                placeholderTextColor={isDark ? '#888' : '#666'}
                value={taskTitle}
                onChangeText={setTaskTitle}
                autoFocus
              />
              
              {categories.length > 0 && (
                <View className="mb-4">
                  <Text className="mb-2">Categoria:</Text>
                  <View className="flex-row flex-wrap">
                    {categories.map(category => (
                      <TouchableOpacity
                        key={category}
                        className={`m-1 py-1 px-3 rounded-full ${
                          selectedCategory === category 
                            ? 'bg-primary' 
                            : isDark ? 'bg-gray-800' : 'bg-gray-200'
                        }`}
                        onPress={() => setSelectedCategory(
                          selectedCategory === category ? undefined : category
                        )}
                      >
                        <Text className={selectedCategory === category ? 'text-white' : ''}>
                          {category}
                        </Text>
                      </TouchableOpacity>
                    ))}
                  </View>
                </View>
              )}
              
              <View className="flex-row justify-end">
                <Button
                  variant="ghost"
                  onPress={onClose}
                  className="mr-2"
                >
                  Cancelar
                </Button>
                
                <Button
                  onPress={handleAddTask}
                  disabled={taskTitle.trim() === ''}
                  className={taskTitle.trim() === '' ? 'opacity-50' : ''}
                >
                  Adicionar
                </Button>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </KeyboardAvoidingView>
      </TouchableWithoutFeedback>
    </Modal>
  );
};

export default AddTaskModal;
EOF