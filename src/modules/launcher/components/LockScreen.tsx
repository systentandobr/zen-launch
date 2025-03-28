import React, { useState, useEffect } from 'react';
import { 
  View, 
  Modal,
  TouchableOpacity, 
  Animated, 
  Easing,
  StyleSheet,
  Dimensions
} from 'react-native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import DigitalClock from './DigitalClock';

interface LockScreenProps {
  visible: boolean;
  onUnlock: () => void;
  showClock?: boolean;
  showDate?: boolean;
}

const { width, height } = Dimensions.get('window');

const LockScreen: React.FC<LockScreenProps> = ({
  visible,
  onUnlock,
  showClock = true,
  showDate = true
}) => {
  const { isDark } = useTheme();
  const [slideAnim] = useState(new Animated.Value(0));
  
  useEffect(() => {
    if (visible) {
      slideAnim.setValue(0);
    }
  }, [visible, slideAnim]);
  
  const handleSlide = (direction: 'up' | 'down') => {
    const toValue = direction === 'up' ? -height : 0;
    
    Animated.timing(slideAnim, {
      toValue,
      duration: 300,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true
    }).start(() => {
      if (direction === 'up') {
        onUnlock();
      }
    });
  };
  
  if (!visible) return null;
  
  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
    >
      <Animated.View 
        className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}
        style={[
          { transform: [{ translateY: slideAnim }] }
        ]}
      >
        <View className="flex-1 items-center justify-center">
          {showClock && (
            <DigitalClock 
              showSeconds={true}
              showDate={showDate}
              is24Hour={true}
              className="mb-10"
            />
          )}
          
          <TouchableOpacity
            className={`p-4 w-16 h-16 rounded-full items-center justify-center ${
              isDark ? 'bg-gray-800' : 'bg-white'
            } shadow-md mt-10`}
            onPress={() => handleSlide('up')}
          >
            <Text className="text-2xl">↑</Text>
          </TouchableOpacity>
          
          <Text className="mt-4 opacity-70">
            Deslize para cima para desbloquear
          </Text>
        </View>
      </Animated.View>
    </Modal>
  );
};

export default LockScreen;
