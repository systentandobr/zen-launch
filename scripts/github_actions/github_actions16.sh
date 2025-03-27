#!/bin/bash

# Script 16 criado automaticamente
echo "Executando claude-ai16.sh"
cat << 'EOF' > src/types/app.d.ts
/**
 * Tipos globais para o aplicativo Zen Launcher
 */

// Tipo para informações de aplicativo
interface AppInfo {
  id: string;
  name: string;
  packageName: string;
  category?: string;
  isRestricted: boolean;
  icon?: any;
}

// Tipo para uso de aplicativo
interface AppUsage {
  packageName: string;
  appName: string;
  usageTime: number; // em minutos
  lastUsed: number; // timestamp
}

// Tipo para notificação
interface Notification {
  id: string;
  appName: string;
  packageName: string;
  title: string;
  content: string;
  timestamp: number;
  isRead?: boolean;
}

// Tipo para tarefa
interface Task {
  id: string;
  title: string;
  completed: boolean;
  category?: string;
  createdAt: number;
  completedAt?: number;
}

// Tipo para informações meteorológicas
interface WeatherData {
  city: string;
  temperature: number;
  condition: string;
  chanceOfRain: number;
  high: number;
  low: number;
  icon?: string;
}

// Tipo para modo de tema
type ThemeMode = 'light' | 'dark' | 'system';

// Tipo para orientação de app
type AppOrientation = 'portrait' | 'landscape' | 'auto';
EOF

cat << 'EOF' > src/types/navigation.d.ts
/**
 * Tipos para navegação
 */

// Tipos para o navegador raiz
type RootStackParamList = {
  Main: undefined;
  Settings: undefined;
  AppSettings: undefined;
  AppDetails: { packageName: string };
  TaskDetails: { taskId: string };
  Notifications: undefined;
  About: undefined;
};

// Tipos para o navegador de abas
type MainTabParamList = {
  Home: undefined;
  Apps: undefined;
  Focus: undefined;
  Tasks: undefined;
  Settings: undefined;
};

// Estende o namespace ReactNavigation para fornecer tipos
declare global {
  namespace ReactNavigation {
    interface RootParamList extends RootStackParamList {}
  }
}
EOF

cat << 'EOF' > src/types/env.d.ts
/**
 * Declarações de ambiente
 */

// Declara variáveis de ambiente
declare module '@env' {
  export const WEATHER_API_KEY: string;
}

// Estende variáveis globais do Node
declare namespace NodeJS {
  interface ProcessEnv {
    NODE_ENV: 'development' | 'production' | 'test';
    WEATHER_API_KEY: string;
  }
}
EOF