import React from 'react';
import { View, ViewProps } from 'react-native';
import { useTheme } from '../theme/ThemeContext';

interface CardProps extends ViewProps {
  children: React.ReactNode;
  className?: string;
}

const Card: React.FC<CardProps> = ({ children, className = '', ...props }) => {
  const { isDark } = useTheme();
  
  return (
    <View 
      className={`rounded-xl p-4 mb-3 ${isDark ? 'bg-card-dark' : 'bg-card-light'} shadow-sm ${className}`}
      {...props}
    >
      {children}
    </View>
  );
};

export default Card;
