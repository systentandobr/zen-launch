#!/bin/bash

# Script 11 criado automaticamente
echo "Executando claude-ai11.sh"
cat << 'EOF' > src/modules/launcher/screens/LauncherHomeScreen.tsx
import React, { useState, useEffect } from 'react';
import { View, ScrollView, StatusBar, SafeAreaView } from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import DigitalClock from '../components/DigitalClock';
import WeatherWidget from '@modules/weather/components/WeatherWidget';
import TaskList from '@modules/tasks/components/TaskList';
import FocusMode from '@modules/focus/components/FocusMode';
import AppUsageChart from '@modules/focus/components/AppUsageChart';
import { Task } from '@modules/tasks/components/TaskList';
import { AppUsage } from '@modules/focus/components/AppUsageChart';

// Mock data para demonstração
const mockTasks: Task[] = [
  { id: '1', title: 'Finalizar relatório', completed: false },
  { id: '2', title: 'Reunião com o time', completed: true },
  { id: '3', title: 'Fazer academia', completed: false },
];

const mockAppUsage: AppUsage[] = [
  { appName: 'Chrome', packageName: 'com.android.chrome', usageTime: 120 },
  { appName: 'Instagram', packageName: 'com.instagram.android', usageTime: 65 },
  { appName: 'WhatsApp', packageName: 'com.whatsapp', usageTime: 45 },
  { appName: 'Gmail', packageName: 'com.google.android.gm', usageTime: 30 },
  { appName: 'YouTube', packageName: 'com.google.android.youtube', usageTime: 25 },
];

const LauncherHomeScreen: React.FC = () => {
  const { isDark } = useTheme();
  const [focusMode, setFocusMode] = useState<boolean>(false);
  const [focusTimeRemaining, setFocusTimeRemaining] = useState<number | undefined>(undefined);
  const [tasks, setTasks] = useState<Task[]>(mockTasks);

  // Efeito para atualizar o timer quando o modo foco estiver ativo
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (focusMode && focusTimeRemaining) {
      interval = setInterval(() => {
        setFocusTimeRemaining(prev => {
          if (prev && prev > 0) {
            return prev - 1;
          } else {
            setFocusMode(false);
            clearInterval(interval);
            return undefined;
          }
        });
      }, 1000);
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [focusMode, focusTimeRemaining]);

  const handleToggleTask = (taskId: string) => {
    setTasks(prev => prev.map(task => 
      task.id === taskId 
        ? { ...task, completed: !task.completed } 
        : task
    ));
  };

  const handleToggleFocusMode = (active: boolean) => {
    setFocusMode(active);
    if (!active) {
      setFocusTimeRemaining(undefined);
    }
  };

  const handleSetFocusDuration = (minutes: number) => {
    setFocusTimeRemaining(minutes * 60);
    setFocusMode(true);
  };

  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <ScrollView 
        className="flex-1 px-4 pt-6"
        showsVerticalScrollIndicator={false}
      >
        <DigitalClock 
          className="mb-6" 
          showSeconds={false} 
          showDate={true}
          is24Hour={true}
        />
        
        <WeatherWidget 
          city="São Paulo"
          temperature={24}
          condition="Clear"
          chanceOfRain={30}
          high={31}
          low={7}
          className="mb-4"
        />
        
        <FocusMode 
          isActive={focusMode}
          onToggle={handleToggleFocusMode}
          onSetDuration={handleSetFocusDuration}
          timeRemaining={focusTimeRemaining}
          className="mb-4"
        />
        
        <TaskList 
          tasks={tasks}
          onToggleTask={handleToggleTask}
          title="Tarefas para hoje"
          className="mb-4"
        />
        
        <AppUsageChart 
          data={mockAppUsage}
          period="day"
          className="mb-4"
        />
      </ScrollView>
    </SafeAreaView>
  );
};

export default LauncherHomeScreen;
EOF