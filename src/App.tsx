import React from 'react';
import { StatusBar, LogBox } from 'react-native';
import { Provider as ReduxProvider } from 'react-redux';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { ThemeProvider } from '@core/ui/theme/ThemeContext';
import RootNavigator from '@navigation/RootNavigator';
import store from '@app/store';

// Ignora warnings específicos
LogBox.ignoreLogs([
  'ViewPropTypes will be removed from React Native',
  'AsyncStorage has been extracted from react-native',
]);

const App: React.FC = () => {
  return (
    <ReduxProvider store={store}>
      <ThemeProvider>
        <SafeAreaProvider>
          <StatusBar translucent backgroundColor="transparent" />
          <RootNavigator />
        </SafeAreaProvider>
      </ThemeProvider>
    </ReduxProvider>
  );
};

export default App;
