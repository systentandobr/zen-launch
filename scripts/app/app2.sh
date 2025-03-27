#!/bin/bash

# Script 2 criado automaticamente
echo "Executando claude-ai2.sh"
cat << 'EOF' > src/core/ui/theme/ThemeContext.tsx
import React, { createContext, useContext, useState, useEffect } from 'react';
import { useColorScheme } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

type ThemeMode = 'light' | 'dark' | 'system';

interface ThemeContextType {
  isDark: boolean;
  themeMode: ThemeMode;
  setThemeMode: (mode: ThemeMode) => void;
}

const ThemeContext = createContext<ThemeContextType>({
  isDark: false,
  themeMode: 'system',
  setThemeMode: () => {},
});

export const useTheme = () => useContext(ThemeContext);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const systemColorScheme = useColorScheme();
  const [themeMode, setThemeMode] = useState<ThemeMode>('system');
  const [isDark, setIsDark] = useState<boolean>(systemColorScheme === 'dark');

  useEffect(() => {
    const loadThemePreference = async () => {
      try {
        const savedTheme = await AsyncStorage.getItem('theme_mode');
        if (savedTheme === 'light' || savedTheme === 'dark' || savedTheme === 'system') {
          setThemeMode(savedTheme as ThemeMode);
        }
      } catch (error) {
        console.log('Error loading theme preference:', error);
      }
    };

    loadThemePreference();
  }, []);

  useEffect(() => {
    // Calculate if dark mode is active based on theme mode
    setIsDark(
      themeMode === 'system' 
        ? systemColorScheme === 'dark' 
        : themeMode === 'dark'
    );
    
    // Save theme preference
    AsyncStorage.setItem('theme_mode', themeMode);
  }, [themeMode, systemColorScheme]);

  return (
    <ThemeContext.Provider
      value={{
        isDark,
        themeMode,
        setThemeMode,
      }}
    >
      {children}
    </ThemeContext.Provider>
  );
};
EOF

cat << 'EOF' > src/core/ui/components/Card.tsx
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
EOF

cat << 'EOF' > src/core/ui/components/Text.tsx
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
EOF

cat << 'EOF' > src/core/ui/components/Button.tsx
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
EOF

cat << 'EOF' > src/core/ui/components/AppIcon.tsx
import React from 'react';
import { Image, ImageProps, View, Pressable, PressableProps } from 'react-native';
import Text from './Text';
import { useTheme } from '../theme/ThemeContext';

interface AppIconProps extends PressableProps {
  name: string;
  icon: any; // Fonte do ícone
  onPress?: () => void;
  size?: 'sm' | 'md' | 'lg';
  isRestricted?: boolean;
  className?: string;
}

const AppIcon: React.FC<AppIconProps> = ({
  name,
  icon,
  onPress,
  size = 'md',
  isRestricted = false,
  className = '',
  ...props
}) => {
  const { isDark } = useTheme();
  
  const getSizeClasses = (): { container: string, icon: object } => {
    switch (size) {
      case 'sm':
        return { 
          container: 'w-14 h-14', 
          icon: { width: 28, height: 28 } 
        };
      case 'lg':
        return { 
          container: 'w-20 h-20', 
          icon: { width: 48, height: 48 } 
        };
      default:
        return { 
          container: 'w-16 h-16', 
          icon: { width: 36, height: 36 } 
        };
    }
  };

  const sizeClasses = getSizeClasses();

  return (
    <View className="items-center">
      <Pressable
        className={`${sizeClasses.container} items-center justify-center rounded-xl mb-1 ${
          isDark ? 'bg-card-dark' : 'bg-card-light'
        } ${isRestricted ? 'opacity-40' : ''} ${className}`}
        onPress={!isRestricted ? onPress : undefined}
        disabled={isRestricted}
        {...props}
      >
        <Image
          source={icon}
          style={sizeClasses.icon}

EOF