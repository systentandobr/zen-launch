import React from 'react';
import { Pressable, PressableProps, Text } from 'react-native';
import { useTheme } from '../theme/ThemeContext';

interface ButtonProps extends PressableProps {
  children: React.ReactNode;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  textClassName?: string;
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  className = '',
  textClassName = '',
  ...props
}) => {
  const { isDark } = useTheme();
  
  const getVariantClasses = (): string => {
    switch (variant) {
      case 'primary':
        return 'bg-primary';
      case 'secondary':
        return 'bg-secondary';
      case 'outline':
        return `border border-2 ${isDark ? 'border-text-dark' : 'border-text-light'} bg-transparent`;
      case 'ghost':
        return 'bg-transparent';
      default:
        return 'bg-primary';
    }
  };

  const getSizeClasses = (): string => {
    switch (size) {
      case 'sm':
        return 'py-1 px-2 rounded-md';
      case 'lg':
        return 'py-3 px-6 rounded-xl';
      default:
        return 'py-2 px-4 rounded-lg';
    }
  };

  const getTextColor = (): string => {
    if (variant === 'outline' || variant === 'ghost') {
      return isDark ? 'text-text-dark' : 'text-text-light';
    }
    return 'text-white';
  };

  return (
    <Pressable
      className={`items-center justify-center ${getVariantClasses()} ${getSizeClasses()} ${className}`}
      {...props}
    >
      {typeof children === 'string' ? (
        <Text className={`font-medium ${getTextColor()} ${textClassName}`}>{children}</Text>
      ) : (
        children
      )}
    </Pressable>
  );
};

export default Button;
