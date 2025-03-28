import React from 'react';
import { Text as RNText, TextProps } from 'react-native';
import { useTheme } from '../theme/ThemeContext';

interface CustomTextProps extends TextProps {
  variant?: 'h1' | 'h2' | 'h3' | 'body' | 'caption';
  className?: string;
}

const Text: React.FC<CustomTextProps> = ({ 
  children, 
  variant = 'body', 
  className = '',
  ...props 
}) => {
  const { isDark } = useTheme();
  
  const getVariantClasses = (): string => {
    switch (variant) {
      case 'h1':
        return 'text-3xl font-bold';
      case 'h2':
        return 'text-2xl font-semibold';
      case 'h3':
        return 'text-xl font-semibold';
      case 'caption':
        return 'text-xs opacity-70';
      default:
        return 'text-base';
    }
  };

  return (
    <RNText 
      className={`${isDark ? 'text-text-dark' : 'text-text-light'} ${getVariantClasses()} ${className}`}
      {...props}
    >
      {children}
    </RNText>
  );
};

export default Text;
