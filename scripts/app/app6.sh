#!/bin/bash

# Script 6 criado automaticamente
echo "Executando claude-ai6.sh"
cat << 'EOF' > src/modules/tasks/components/TaskList.tsx
import React from 'react';
import { View, FlatList, Pressable } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import { useTheme } from '@core/ui/theme/ThemeContext';

export interface Task {
  id: string;
  title: string;
  completed: boolean;
  category?: string;
}

interface TaskListProps {
  tasks: Task[];
  onToggleTask: (taskId: string) => void;
  onDeleteTask?: (taskId: string) => void;
  title?: string;
  className?: string;
}

const TaskList: React.FC<TaskListProps> = ({
  tasks,
  onToggleTask,
  onDeleteTask,
  title = 'To-do',
  className = '',
}) => {
  const { isDark } = useTheme();

  const renderTask = ({ item }: { item: Task }) => {
    return (
      <Pressable
        className={`flex-row items-center py-3 px-2 mb-2 rounded-lg ${
          isDark ? 'bg-gray-800' : 'bg-gray-100'
        }`}
        onPress={() => onToggleTask(item.id)}
      >
        <View 
          className={`w-6 h-6 rounded-full mr-3 border ${
            item.completed 
              ? 'bg-primary border-primary' 
              : isDark ? 'border-gray-600' : 'border-gray-400'
          }`}
        >
          {item.completed && (
            <Text className="text-white text-center">✓</Text>
          )}
        </View>
        
        <Text 
          className={`flex-1 ${
            item.completed 
              ? 'line-through opacity-50' 
              : ''
          }`}
        >
          {item.title}
        </Text>
        
        {onDeleteTask && (
          <Pressable 
            className="p-2" 
            onPress={() => onDeleteTask(item.id)}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <Text>✕</Text>
          </Pressable>
        )}
      </Pressable>
    );
  };

  return (
    <Card className={className}>
      <View className="flex-row justify-between items-center mb-4">
        <Text variant="h3">{title}</Text>
        <Text className="text-primary">
          {tasks.filter(t => t.completed).length}/{tasks.length}
        </Text>
      </View>
      
      {tasks.length > 0 ? (
        <FlatList
          data={tasks}
          renderItem={renderTask}
          keyExtractor={item => item.id}
          scrollEnabled={false}
        />
      ) : (
        <Text className="text-center py-4 italic opacity-50">
          Nenhuma tarefa adicionada
        </Text>
      )}
    </Card>
  );
};

export default TaskList;
EOF