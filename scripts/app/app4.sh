#!/bin/bash

# Script 4 criado automaticamente
echo "Executando claude-ai4.sh"
cat << 'EOF' > src/modules/weather/components/WeatherWidget.tsx
import React from 'react';
import { View } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import { useTheme } from '@core/ui/theme/ThemeContext';

interface WeatherWidgetProps {
  city: string;
  temperature: number;
  condition: string;
  chanceOfRain: number;
  high: number;
  low: number;
  className?: string;
}

const WeatherWidget: React.FC<WeatherWidgetProps> = ({
  city,
  temperature,
  condition,
  chanceOfRain,
  high,
  low,
  className = '',
}) => {
  const { isDark } = useTheme();

  // Ícone baseado na condição (pode ser substituído por ícones reais)
  const getWeatherIcon = () => {
    return '☀️'; // Exemplo simples, deve ser substituído por um componente de ícone real
  };

  return (
    <Card className={`flex-row justify-between items-center ${className}`}>
      <View className="flex-1">
        <View className="flex-row items-center">
          <Text variant="h2" className="mr-2">{city}</Text>
          <Text className="text-2xl">{getWeatherIcon()}</Text>
        </View>
        <Text variant="body">{condition}</Text>
        <Text variant="caption" className={isDark ? 'text-text-secondary-dark' : 'text-text-secondary-light'}>
          Chance de chuva: {chanceOfRain}%
        </Text>
      </View>
      
      <View className="items-end">
        <Text variant="h1" className="text-4xl font-bold">{temperature}°</Text>
        <View className="flex-row mt-1">
          <Text className="mr-2">↑ {high}°</Text>
          <Text>↓ {low}°</Text>
        </View>
      </View>
    </Card>
  );
};

export default WeatherWidget;
EOF