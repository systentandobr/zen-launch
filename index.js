import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import { registerRootComponent } from 'expo';

AppRegistry.registerComponent(appName, () => App);

// Para compatibilidade com Expo
registerRootComponent(App);
