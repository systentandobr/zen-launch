import React, { useState, useEffect } from 'react';
import { View } from 'react-native';
import Text from '@core/ui/components/Text';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface DigitalClockProps {
  showSeconds?: boolean;
  showDate?: boolean;
  is24Hour?: boolean;
  className?: string;
}

const DigitalClock: React.FC<DigitalClockProps> = ({
  showSeconds = false,
  showDate = true,
  is24Hour = true,
  className = '',
}) => {
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const formatTime = () => {
    const timeFormat = is24Hour 
      ? (showSeconds ? 'HH:mm:ss' : 'HH:mm')
      : (showSeconds ? 'hh:mm:ss a' : 'hh:mm a');
    
    return format(currentTime, timeFormat);
  };

  const formatDate = () => {
    return format(currentTime, "EEEE, d 'de' MMMM", { locale: ptBR });
  };

  return (
    <View className={`items-center ${className}`}>
      <Text variant="h1" className="text-5xl font-bold tracking-tight">
        {formatTime()}
      </Text>
      
      {showDate && (
        <Text variant="body" className="mt-1 first-letter:uppercase">
          {formatDate()}
        </Text>
      )}
    </View>
  );
};

export default DigitalClock;
