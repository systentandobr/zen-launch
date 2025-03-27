#!/bin/bash

# Script 9 criado automaticamente
echo "Executando claude-ai9.sh"
cat << 'EOF' > src/modules/focus/components/AppUsageChart.tsx
import React from 'react';
import { View } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import { useTheme } from '@core/ui/theme/ThemeContext';

export interface AppUsage {
  appName: string;
  packageName: string;
  usageTime: number; // em minutos
  icon?: any;
}

interface AppUsageChartProps {
  data: AppUsage[];
  title?: string;
  period?: 'day' | 'week' | 'month';
  className?: string;
}

const AppUsageChart: React.FC<AppUsageChartProps> = ({
  data,
  title = 'Uso de Apps',
  period = 'day',
  className = '',
}) => {
  const { isDark } = useTheme();
  
  // Ordena os apps por tempo de uso (decrescente)
  const sortedData = [...data].sort((a, b) => b.usageTime - a.usageTime);
  
  // Pega apenas os 5 apps mais usados
  const topApps = sortedData.slice(0, 5);
  
  // Calcula o tempo total para percentuais
  const totalTime = data.reduce((sum, app) => sum + app.usageTime, 0);
  
  // Formata o tempo (minutos) para exibição
  const formatTime = (minutes: number) => {
    if (minutes < 60) {
      return `${minutes}m`;
    }
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`;
  };

  const getPeriodLabel = () => {
    switch (period) {
      case 'week': return 'esta semana';
      case 'month': return 'este mês';
      default: return 'hoje';
    }
  };

  return (
    <Card className={`${className}`}>
      <View className="flex-row justify-between items-center mb-4">
        <Text variant="h3">{title}</Text>
        <Text variant="caption">{getPeriodLabel()}</Text>
      </View>
      
      {topApps.length > 0 ? (
        <>
          {topApps.map((app, index) => {
            const percentage = (app.usageTime / totalTime) * 100;
            
            return (
              <View key={app.packageName || index} className="mb-3">
                <View className="flex-row justify-between items-center mb-1">
                  <Text>{app.appName}</Text>
                  <Text variant="caption">{formatTime(app.usageTime)}</Text>
                </View>
                
                <View 
                  className={`w-full h-2 rounded-full ${
                    isDark ? 'bg-gray-800' : 'bg-gray-200'
                  }`}
                >
                  <View 
                    className="h-full rounded-full bg-primary"
                    style={{ width: `${percentage}%` }}
                  />
                </View>
              </View>
            );
          })}
          
          <View className="items-center mt-2">
            <Text variant="caption">
              Tempo total: {formatTime(totalTime)}
            </Text>
          </View>
        </>
      ) : (
        <Text className="text-center py-4 italic opacity-50">
          Nenhum dado de uso disponível
        </Text>
      )}
    </Card>
  );
};

export default AppUsageChart;
EOF