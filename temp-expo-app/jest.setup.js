import 'react-native-gesture-handler/jestSetup';

// Mock de módulos nativos que podem causar problemas nos testes
jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper');

// Mock para o módulo Reanimated
jest.mock('react-native-reanimated', () => {
  const Reanimated = require('react-native-reanimated/mock');
  Reanimated.default.call = () => {};
  return Reanimated;
});

// Mock para o StatusBar
jest.mock('expo-status-bar', () => ({
  StatusBar: jest.fn().mockImplementation(() => null),
}));

// Mock para o AsyncStorage
jest.mock('@react-native-async-storage/async-storage', () => ({
  setItem: jest.fn(),
  getItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}));

// Mock para o módulo de fontes
jest.mock('expo-font', () => ({
  loadAsync: jest.fn().mockResolvedValue(true),
}));
