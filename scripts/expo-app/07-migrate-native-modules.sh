#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

TEMP_DIR="temp-expo-app"

# Verificar se o diretório temporário existe
if [ ! -d "$TEMP_DIR" ]; then
  echo -e "${YELLOW}Diretório $TEMP_DIR não encontrado. Execute o script 01-create-expo-app.sh primeiro.${NC}"
  exit 1
fi

echo -e "${GREEN}Configurando migração de módulos nativos para Expo...${NC}"

cd $TEMP_DIR

# Criar diretório para scripts
mkdir -p scripts

# Lista de módulos comuns do React Native e seus equivalentes no Expo
echo -e "${BLUE}Guia de substituição de módulos nativos para Expo:${NC}"
cat << 'END_GUIDE' > native-modules-migration-guide.md
# Guia de Migração de Módulos Nativos para Expo

## Módulos Comuns e Equivalentes no Expo

| Módulo React Native | Equivalente no Expo | Observações |
|---------------------|---------------------|-------------|
| `react-native-camera` | `expo-camera` | Acesso à câmera |
| `react-native-maps` | `expo-location` + `react-native-maps` | O Expo inclui suporte ao react-native-maps |
| `react-native-webview` | `react-native-webview` | O Expo inclui suporte |
| `react-native-fs` | `expo-file-system` | Operações com arquivos |
| `react-native-device-info` | `expo-device` | Informações do dispositivo |
| `react-native-permissions` | `expo-permissions` | Gerenciamento de permissões |
| `react-native-push-notification` | `expo-notifications` | Notificações push |
| `react-native-geolocation` | `expo-location` | Acesso à localização |
| `react-native-image-picker` | `expo-image-picker` | Seleção de imagens |
| `react-native-video` | `expo-av` | Reprodução de áudio/vídeo |
| `react-native-sqlite-storage` | `expo-sqlite` | Banco de dados SQLite |
| `react-native-share` | `expo-sharing` | Compartilhamento |
| `react-native-fbsdk` | `expo-facebook` | Integração com Facebook |
| `react-native-google-signin` | `expo-auth-session` | Autenticação com Google |
| `react-native-svg` | `react-native-svg` | O Expo inclui suporte |
| `react-native-biometrics` | `expo-local-authentication` | Autenticação biométrica |
| `react-native-background-timer` | `expo-background-fetch` | Tarefas em segundo plano |
| `react-native-keychain` | `expo-secure-store` | Armazenamento seguro |
| `react-native-gesture-handler` | `react-native-gesture-handler` | O Expo inclui suporte |
| `react-native-reanimated` | `react-native-reanimated` | O Expo inclui suporte |

## Configuração de plugins Expo

Para alguns módulos nativos, o Expo requer a configuração de plugins no arquivo `app.json` ou `app.config.js`.

Exemplo de configuração no `app.json`:

```json
{
  "expo": {
    "plugins": [
      [
        "expo-camera",
        {
          "cameraPermission": "Permita o acesso à câmera para tirar fotos."
        }
      ],
      [
        "expo-location",
        {
          "locationAlwaysAndWhenInUsePermission": "Permita o acesso à sua localização."
        }
      ]
    ]
  }
}
```

## Módulos não suportados

Se um módulo nativo não for suportado pelo Expo, você terá algumas opções:

1. Usar um módulo alternativo que seja suportado
2. Implementar uma solução diferente usando APIs disponíveis
3. Usar o EAS (Expo Application Services) com configuração de desenvolvimento
4. Criar um módulo de Expo personalizado (avançado)
END_GUIDE

echo -e "${GREEN}Guia de migração de módulos nativos criado: native-modules-migration-guide.md${NC}"

# Criar script de ajuda para identificar bibliotecas incompatíveis
cat << 'END_COMPAT_SCRIPT' > scripts/check-expo-compatibility.js
#!/usr/bin/env node

/**
 * Script para verificar a compatibilidade de dependências com o Expo
 * 
 * Uso: 
 * 1. Copie o package.json do seu projeto React Native para o mesmo diretório que este script
 * 2. Execute: node check-expo-compatibility.js
 */

const fs = require('fs');
const path = require('path');

// Lista de módulos conhecidos por serem incompatíveis ou que precisam de configuração especial
const INCOMPATIBLE_MODULES = [
  'react-native-bluetooth-serial',
  'react-native-ble-manager',
  'react-native-wifi-reborn',
  'react-native-wifi-p2p',
  'react-native-firebase', // Usar expo-firebase-analytics, expo-firebase-core, etc.
  'react-native-touch-id', // Usar expo-local-authentication
  'react-native-sms', // Não há suporte direto no Expo
  'react-native-iap', // Usar expo-in-app-purchases
  'react-native-background-geolocation',
  'react-native-background-fetch',
  'react-native-track-player',
  'react-native-quick-base64',
  'react-native-audio-toolkit',
  'react-native-nfc-manager',
  'react-native-signature-capture',
  'react-native-print',
  'react-native-ble-plx',
  'realm',
];

// Módulos que exigem configuração especial no Expo
const SPECIAL_CONFIG_MODULES = [
  'react-native-reanimated', // Precisa de plugin no babel.config.js
  'react-native-maps', // Precisa de configuração no app.json
  'react-native-webview', // Compatível, mas pode precisar de configuração
  'react-native-svg', // Compatível, mas pode precisar importar de forma diferente
  '@react-native-async-storage/async-storage', // Compatível, mas gerenciado pelo Expo
  'react-native-gesture-handler', // Compatível, mas precisa ser importado no topo do arquivo de entrada
  'lottie-react-native', // Usar expo-lottie
];

// Módulos que têm equivalentes diretos no Expo
const EXPO_EQUIVALENT_MODULES = {
  'react-native-camera': 'expo-camera',
  'react-native-permissions': 'expo-permissions',
  'react-native-fs': 'expo-file-system',
  'react-native-device-info': 'expo-device',
  'react-native-image-picker': 'expo-image-picker',
  'react-native-geolocation-service': 'expo-location',
  'react-native-video': 'expo-av',
  'react-native-sqlite-storage': 'expo-sqlite',
  'react-native-share': 'expo-sharing',
  'react-native-fbsdk': 'expo-facebook',
  'react-native-push-notification': 'expo-notifications',
  'react-native-biometrics': 'expo-local-authentication',
  'react-native-keychain': 'expo-secure-store',
  'react-native-splash-screen': 'expo-splash-screen',
  '@react-native-community/netinfo': 'expo-network',
  '@react-native-community/datetimepicker': '@react-native-community/datetimepicker', // Suportado pelo Expo
};

try {
  // Ler o package.json do projeto React Native
  const packageJsonPath = path.join(process.cwd(), 'package.json');
  if (!fs.existsSync(packageJsonPath)) {
    console.error('\x1b[31mErro: package.json não encontrado no diretório atual.\x1b[0m');
    process.exit(1);
  }

  const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
  const dependencies = { ...packageJson.dependencies, ...packageJson.devDependencies };

  console.log('\x1b[36m=== Análise de Compatibilidade com Expo ===\x1b[0m\n');

  // Arrays para categorizar as dependências
  const incompatible = [];
  const needsSpecialConfig = [];
  const hasExpoEquivalent = {};
  
  // Verificar cada dependência
  for (const dep in dependencies) {
    if (INCOMPATIBLE_MODULES.includes(dep)) {
      incompatible.push(dep);
    } else if (SPECIAL_CONFIG_MODULES.includes(dep)) {
      needsSpecialConfig.push(dep);
    } else if (dep in EXPO_EQUIVALENT_MODULES) {
      hasExpoEquivalent[dep] = EXPO_EQUIVALENT_MODULES[dep];
    }
  }

  // Exibir resultados
  if (incompatible.length > 0) {
    console.log('\x1b[31m=== Dependências Incompatíveis com Expo ===\x1b[0m');
    incompatible.forEach(dep => console.log(`- ${dep}`));
    console.log('\nEstas dependências não são suportadas pelo Expo e precisarão ser substituídas ou removidas.\n');
  }

  if (Object.keys(hasExpoEquivalent).length > 0) {
    console.log('\x1b[33m=== Dependências com Equivalentes no Expo ===\x1b[0m');
    for (const dep in hasExpoEquivalent) {
      console.log(`- ${dep} -> ${hasExpoEquivalent[dep]}`);
    }
    console.log('\nEstas dependências devem ser substituídas pelos equivalentes do Expo.\n');
  }

  if (needsSpecialConfig.length > 0) {
    console.log('\x1b[33m=== Dependências que Precisam de Configuração Especial ===\x1b[0m');
    needsSpecialConfig.forEach(dep => console.log(`- ${dep}`));
    console.log('\nEstas dependências são compatíveis com o Expo, mas podem precisar de configuração adicional.\n');
  }

  console.log('\x1b[32m=== Resumo ===\x1b[0m');
  console.log(`Total de dependências analisadas: ${Object.keys(dependencies).length}`);
  console.log(`Incompatíveis: ${incompatible.length}`);
  console.log(`Precisam de substituição: ${Object.keys(hasExpoEquivalent).length}`);
  console.log(`Precisam de configuração especial: ${needsSpecialConfig.length}`);
  
  if (incompatible.length === 0 && Object.keys(hasExpoEquivalent).length === 0) {
    console.log('\n\x1b[32mSeu projeto parece ser compatível com o Expo com poucas ou nenhuma alteração!\x1b[0m');
  } else if (incompatible.length > 0) {
    console.log('\n\x1b[31mSeu projeto tem dependências incompatíveis que precisarão ser resolvidas antes da migração para o Expo.\x1b[0m');
  } else {
    console.log('\n\x1b[33mSeu projeto precisa de algumas modificações, mas a migração para o Expo é viável.\x1b[0m');
  }

} catch (error) {
  console.error('\x1b[31mErro ao analisar o package.json:\x1b[0m', error);
}
END_COMPAT_SCRIPT

chmod +x scripts/check-expo-compatibility.js

echo -e "${GREEN}Script de verificação de compatibilidade criado: scripts/check-expo-compatibility.js${NC}"
echo -e "${YELLOW}Para verificar a compatibilidade das dependências do seu projeto React Native, copie o package.json para o diretório do script e execute: node scripts/check-expo-compatibility.js${NC}"

# Criar exemplo de configuração de app.json com plugins comuns
echo -e "${GREEN}Criando exemplo de configuração de app.json com plugins comuns...${NC}"
cat << 'END_APP_PLUGINS' > app-plugins-example.json
{
  "expo": {
    "name": "YourAppName",
    "slug": "your-app-slug",
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
      "bundleIdentifier": "com.yourcompany.yourappname",
      "buildNumber": "1.0.0",
      "infoPlist": {
        "NSCameraUsageDescription": "This app uses the camera to take photos.",
        "NSPhotoLibraryUsageDescription": "This app needs access to your photos.",
        "NSLocationWhenInUseUsageDescription": "This app uses your location to provide nearby services."
      }
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#FFFFFF"
      },
      "package": "com.yourcompany.yourappname",
      "versionCode": 1,
      "permissions": [
        "CAMERA",
        "READ_EXTERNAL_STORAGE",
        "WRITE_EXTERNAL_STORAGE",
        "ACCESS_FINE_LOCATION"
      ]
    },
    "web": {
      "favicon": "./assets/favicon.png"
    },
    "plugins": [
      [
        "expo-camera",
        {
          "cameraPermission": "Allow $(PRODUCT_NAME) to access your camera."
        }
      ],
      [
        "expo-location",
        {
          "locationAlwaysAndWhenInUsePermission": "Allow $(PRODUCT_NAME) to use your location."
        }
      ],
      [
        "expo-barcode-scanner",
        {
          "cameraPermission": "Allow $(PRODUCT_NAME) to access your camera to scan barcodes."
        }
      ],
      [
        "expo-media-library",
        {
          "photosPermission": "Allow $(PRODUCT_NAME) to access your photos.",
          "savePhotosPermission": "Allow $(PRODUCT_NAME) to save photos.",
          "isAccessMediaLocationEnabled": true
        }
      ],
      [
        "expo-notifications",
        {
          "icon": "./assets/notification-icon.png",
          "color": "#ffffff"
        }
      ],
      "expo-document-picker",
      "expo-file-system",
      "expo-sharing"
    ]
  }
}
END_APP_PLUGINS

echo -e "${GREEN}Exemplo de configuração app.json criado: app-plugins-example.json${NC}"
echo -e "${YELLOW}Execute o próximo script para finalizar a migração${NC}"

cd ..
chmod +x scripts/expo-app/07-migrate-native-modules.sh
