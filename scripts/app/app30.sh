#!/bin/bash

# Script 30 criado automaticamente
echo "Executando claude-ai30.sh"
cat << 'EOF' > src/modules/tasks/screens/TasksScreen.tsx
import React, { useState } from 'react';
import { View, ScrollView, SafeAreaView, StatusBar, TouchableOpacity } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import TaskList from '../components/TaskList';
import AddTaskModal from '../components/AddTaskModal';
import useTasks from '../hooks/useTasks';

const TasksScreen: React.FC = () => {
  const { isDark } = useTheme();
  const { 
    tasks, 
    addTask, 
    toggleTaskCompletion, 
    deleteTask, 
    getPendingTasks,
    getCompletedTasks
  } = useTasks();
  
  const [modalVisible, setModalVisible] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'pending' | 'completed'>('all');
  
  const handleAddTask = (title: string, category?: string) => {
    addTask(title, category);
  };
  
  const renderTasks = () => {
    let tasksToRender;
    
    switch (activeTab) {
      case 'pending':
        tasksToRender = getPendingTasks();
        break;
      case 'completed':
        tasksToRender = getCompletedTasks();
        break;
      default:
        tasksToRender = tasks;
    }
    
    if (tasksToRender.length === 0) {
      return (
        <View className="items-center py-8">
          <Text className="opacity-50 italic">
            {activeTab === 'all' 
              ? 'Nenhuma tarefa adicionada' 
              : activeTab === 'pending' 
                ? 'Nenhuma tarefa pendente' 
                : 'Nenhuma tarefa concluída'}
          </Text>
        </View>
      );
    }
    
    return (
      <TaskList
        tasks={tasksToRender}
        onToggleTask={toggleTaskCompletion}
        onDeleteTask={deleteTask}
        title=""
      />
    );
  };
  
  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <View className="px-4 pt-6 pb-2">
        <View className="flex-row justify-between items-center mb-6">
          <Text variant="h2">Tarefas</Text>
          
          <Button
            onPress={() => setModalVisible(true)}
            variant="primary"
            size="sm"
          >
            Adicionar
          </Button>
        </View>
        
        <View className={`flex-row mb-4 p-1 rounded-xl ${isDark ? 'bg-gray-800' : 'bg-gray-200'}`}>
          {['all', 'pending', 'completed'].map((tab) => (
            <TouchableOpacity
              key={tab}
              className={`flex-1 py-2 rounded-lg items-center ${
                activeTab === tab 
                  ? isDark ? 'bg-card-dark' : 'bg-white' 
                  : 'bg-transparent'
              }`}
              onPress={() => setActiveTab(tab as 'all' | 'pending' | 'completed')}
            >
              <Text className={activeTab === tab ? 'font-medium' : 'opacity-70'}>
                {tab === 'all' ? 'Todas' : tab === 'pending' ? 'Pendentes' : 'Concluídas'}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
        
        <ScrollView className="mb-20">
          {renderTasks()}
        </ScrollView>
      </View>
      
      <AddTaskModal
        visible={modalVisible}
        onClose={() => setModalVisible(false)}
        onAddTask={handleAddTask}
      />
    </SafeAreaView>
  );
};

export default TasksScreen;
EOF