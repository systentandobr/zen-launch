import React, { useState } from 'react';
import { View, Switch } from 'react-native';
import Card from '@core/ui/components/Card';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import { useTheme } from '@core/ui/theme/ThemeContext';

interface FocusModeProps {
  isActive: boolean;
  onToggle: (value: boolean) => void;
  onSetDuration: (minutes: number) => void;
  timeRemaining?: number; // em segundos
  className?: string;
}

const FocusMode: React.FC<FocusModeProps> = ({
  isActive,
  onToggle,
  onSetDuration,
  timeRemaining,
  className = '',
}) => {
  const { isDark } = useTheme();
  const [selectedDuration, setSelectedDuration] = useState<number>(60); // Padrão: 60 minutos

  const formatTimeRemaining = (seconds?: number) => {
    if (!seconds) return '00:00';
    
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const durationOptions = [30, 60, 90, 120];

  return (
    <Card className={`${className}`}>
      <View className="flex-row justify-between items-center mb-4">
        <Text variant="h3">Modo Foco</Text>
        <Switch
          value={isActive}
          onValueChange={onToggle}
          trackColor={{ false: '#767577', true: '#C1C1C1' }}
          thumbColor={isActive ? '#FF5C5C' : '#f4f3f4'}
        />
      </View>
      
      {isActive && timeRemaining ? (
        <View className="items-center py-4">
          <Text variant="h2" className="text-4xl mb-2">
            {formatTimeRemaining(timeRemaining)}
          </Text>
          <Text>Tempo restante no modo foco</Text>
          
          <Button 
            variant="outline" 
            className="mt-4" 
            onPress={() => onToggle(false)}
          >
            Desativar modo foco
          </Button>
        </View>
      ) : (
        <View>
          <Text className="mb-3">Selecione a duração:</Text>
          
          <View className="flex-row flex-wrap justify-between mb-4">
            {durationOptions.map(duration => (
              <Button
                key={duration}
                variant={selectedDuration === duration ? 'primary' : 'outline'}
                size="sm"
                className="mb-2 w-1/5"
                onPress={() => setSelectedDuration(duration)}
              >
                {`${duration}m`}
              </Button>
            ))}
          </View>
          
          <Button
            onPress={() => onSetDuration(selectedDuration)}
            disabled={!selectedDuration}
            className={!selectedDuration ? 'opacity-50' : ''}
          >
            Iniciar modo foco
          </Button>
        </View>
      )}
    </Card>
  );
};

export default FocusMode;
