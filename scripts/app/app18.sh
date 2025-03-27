#!/bin/bash

# Script 18 criado automaticamente
echo "Executando claude-ai18.sh"
cat << 'EOF' > src/App.tsx
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
EOF

cat << 'EOF' > index.js
import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import { registerRootComponent } from 'expo';

AppRegistry.registerComponent(appName, () => App);

// Para compatibilidade com Expo
registerRootComponent(App);
EOF

cat << 'EOF' > app.json
{
  "name": "MinimalLauncher",
  "displayName": "Zen Launcher"
}
EOF