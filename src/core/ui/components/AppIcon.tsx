import React from 'react';
import { Image, View, Pressable, PressableProps } from 'react-native';
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
          className={isRestricted ? 'opacity-50' : ''}
        />
      </Pressable>
      <Text 
        variant="caption" 
        className={`text-center mt-1 ${isRestricted ? 'opacity-50' : ''}`}
        numberOfLines={1}
      >
        {name}
      </Text>
    </View>
  );
};

export default AppIcon;
