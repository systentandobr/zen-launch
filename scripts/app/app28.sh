#!/bin/bash

# Script 28 criado automaticamente
echo "Executando claude-ai28.sh"
cat << 'EOF' > src/modules/tasks/hooks/useTasks.ts
import { useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Task } from '../components/TaskList';

const TASKS_STORAGE_KEY = '@MinimalLauncher:tasks';

export const useTasks = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const loadTasks = async () => {
      try {
        setIsLoading(true);
        const storedTasks = await AsyncStorage.getItem(TASKS_STORAGE_KEY);
        
        if (storedTasks) {
          setTasks(JSON.parse(storedTasks));
        }
      } catch (error) {
        console.error('Erro ao carregar tarefas:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadTasks();
  }, []);

  const saveTasks = async (updatedTasks: Task[]) => {
    try {
      await AsyncStorage.setItem(TASKS_STORAGE_KEY, JSON.stringify(updatedTasks));
    } catch (error) {
      console.error('Erro ao salvar tarefas:', error);
    }
  };

  const addTask = async (title: string, category?: string) => {
    const newTask: Task = {
      id: Date.now().toString(),
      title,
      completed: false,
      category
    };

    const updatedTasks = [...tasks, newTask];
    setTasks(updatedTasks);
    await saveTasks(updatedTasks);
    
    return newTask;
  };

  const updateTask = async (id: string, updates: Partial<Omit<Task, 'id'>>) => {
    const updatedTasks = tasks.map(task => 
      task.id === id ? { ...task, ...updates } : task
    );
    
    setTasks(updatedTasks);
    await saveTasks(updatedTasks);
  };

  const toggleTaskCompletion = async (id: string) => {
    const updatedTasks = tasks.map(task => 
      task.id === id ? { ...task, completed: !task.completed } : task
    );
    
    setTasks(updatedTasks);
    await saveTasks(updatedTasks);
  };

  const deleteTask = async (id: string) => {
    const updatedTasks = tasks.filter(task => task.id !== id);
    setTasks(updatedTasks);
    await saveTasks(updatedTasks);
  };

  const getTasksByCategory = (category?: string) => {
    if (!category) return tasks;
    return tasks.filter(task => task.category === category);
  };

  const getCompletedTasks = () => {
    return tasks.filter(task => task.completed);
  };

  const getPendingTasks = () => {
    return tasks.filter(task => !task.completed);
  };

  return {
    tasks,
    isLoading,
    addTask,
    updateTask,
    toggleTaskCompletion,
    deleteTask,
    getTasksByCategory,
    getCompletedTasks,
    getPendingTasks
  };
};

export default useTasks;
EOF