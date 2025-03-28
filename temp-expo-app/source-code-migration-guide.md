# Guia de Migração de Código-Fonte para Expo

## Passos para migrar seu código React Native para Expo

### 1. Estrutura de Diretórios

Recomendamos a seguinte estrutura para seu projeto Expo:

```
/
├── assets/             # Imagens, fontes e outros recursos estáticos
├── src/
│   ├── components/     # Componentes React reutilizáveis
│   ├── screens/        # Telas da aplicação
│   ├── navigation/     # Configuração de navegação
│   ├── hooks/          # Custom hooks
│   ├── contexts/       # Context API providers
│   ├── services/       # Serviços (API, etc)
│   ├── utils/          # Funções utilitárias
│   ├── config/         # Configurações
│   └── types/          # Definições de tipos TypeScript
└── app.json           # Configuração do Expo
```

### 2. Modificações na Importação de Arquivos

Ao migrar para o Expo, você precisará ajustar alguns imports:

1. **Importação de Imagens**:
   - Antes: `import image from './assets/image.png'`
   - Depois: `import image from '../assets/image.png'` (ajuste o caminho conforme necessário)

2. **Componentes Nativos**:
   - Antes: `import { Camera } from 'react-native-camera'`
   - Depois: `import { Camera } from 'expo-camera'`

### 3. APIs Específicas do Expo

Alguns recursos do React Native têm equivalentes no Expo:

- **Fontes**: Use `expo-font` em vez de soluções personalizadas
  ```js
  import * as Font from 'expo-font';
  
  await Font.loadAsync({
    'custom-font': require('../assets/fonts/custom-font.ttf'),
  });
  ```

- **Permissões**: Use `expo-permissions` em vez de soluções específicas por plataforma
  ```js
  import * as Permissions from 'expo-permissions';
  
  const { status } = await Permissions.askAsync(Permissions.CAMERA);
  ```

### 4. Modificações em Componentes Nativos

Se você usa componentes nativos que exigem ligação (linking) ou código nativo, precisará:

1. Verificar se há um equivalente no Expo SDK
2. Usar a configuração de plugins no `app.json` para esses módulos
3. Para módulos sem suporte, considere reescrever a funcionalidade ou usar o desenvolvimento com EAS

### 5. Ponto de Entrada da Aplicação

O arquivo principal do Expo é `App.js` ou `App.tsx` na raiz do projeto:

```jsx
import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider } from 'react-native-safe-area-context';
// Importe seu componente principal ou navegação
import MainNavigator from './src/navigation/MainNavigator';

export default function App() {
  return (
    <SafeAreaProvider>
      <MainNavigator />
      <StatusBar style="auto" />
    </SafeAreaProvider>
  );
}
```

### 6. Adaptação para TypeScript

Se seu projeto original não usava TypeScript, o template Expo com TypeScript requer:

1. Renomear arquivos `.js` para `.tsx` (para componentes) ou `.ts` (para código não-React)
2. Adicionar tipos a propriedades de componentes e funções
3. Criar tipos para suas estruturas de dados em `src/types/`

### 7. Uso de Expo Constants e Ambiente

Para configurações de ambiente, use o objeto `Constants` do Expo:

```js
import Constants from 'expo-constants';

// Acessar variáveis de ambiente e constantes
const apiUrl = Constants.expoConfig?.extra?.apiUrl || 'https://api.default.com';
```

### 8. Configuração do app.json

Configure seu `app.json` com as informações necessárias:

```json
{
  "expo": {
    "name": "Seu App",
    "slug": "seu-app",
    "version": "1.0.0",
    "orientation": "portrait",
    "icon": "./assets/icon.png",
    "splash": {
      "image": "./assets/splash.png",
      "resizeMode": "contain",
      "backgroundColor": "#ffffff"
    },
    "updates": {
      "fallbackToCacheTimeout": 0
    },
    "assetBundlePatterns": [
      "**/*"
    ],
    "ios": {
      "supportsTablet": true,
      "bundleIdentifier": "com.seuapp"
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#FFFFFF"
      },
      "package": "com.seuapp"
    },
    "plugins": []
  }
}
```
