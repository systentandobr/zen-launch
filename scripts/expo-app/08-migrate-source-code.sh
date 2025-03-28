#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

TEMP_DIR="temp-expo-app"
SOURCE_DIR="../src" # Caminho para o código fonte do projeto React Native

# Verificar se o diretório temporário existe
if [ ! -d "$TEMP_DIR" ]; then
  echo -e "${YELLOW}Diretório $TEMP_DIR não encontrado. Execute o script 01-create-expo-app.sh primeiro.${NC}"
  exit 1
fi

echo -e "${GREEN}Configurando migração de código-fonte para Expo...${NC}"

# Criar diretório de scripts se não existir
if [ ! -d "$TEMP_DIR/scripts" ]; then
  echo -e "${GREEN}Criando diretório de scripts...${NC}"
  mkdir -p "$TEMP_DIR/scripts"
fi

# Verificar se o diretório de origem existe
if [ ! -d "$SOURCE_DIR" ]; then
  echo -e "${YELLOW}Aviso: Diretório de origem ($SOURCE_DIR) não encontrado.${NC}"
  echo -e "${YELLOW}Este script espera que o código fonte do projeto React Native esteja em $SOURCE_DIR.${NC}"
  echo -e "${YELLOW}Você pode ajustar a variável SOURCE_DIR no início do script para apontar para o caminho correto.${NC}"
  
  # Criar guia de migração mesmo sem o diretório de origem
  echo -e "${GREEN}Criando guia de migração de código-fonte...${NC}"
else
  echo -e "${GREEN}Diretório de origem encontrado: $SOURCE_DIR${NC}"
  echo -e "${GREEN}Criando guia de migração de código-fonte...${NC}"
fi

# Resto do script permanece o mesmo...
#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

TEMP_DIR="temp-expo-app"
SOURCE_DIR="../src" # Caminho para o código fonte do projeto React Native

# Verificar se o diretório temporário existe
if [ ! -d "$TEMP_DIR" ]; then
  echo -e "${YELLOW}Diretório $TEMP_DIR não encontrado. Execute o script 01-create-expo-app.sh primeiro.${NC}"
  exit 1
fi

echo -e "${GREEN}Configurando migração de código-fonte para Expo...${NC}"

# Verificar se o diretório de origem existe
if [ ! -d "$SOURCE_DIR" ]; then
  echo -e "${YELLOW}Aviso: Diretório de origem ($SOURCE_DIR) não encontrado.${NC}"
  echo -e "${YELLOW}Este script espera que o código fonte do projeto React Native esteja em $SOURCE_DIR.${NC}"
  echo -e "${YELLOW}Você pode ajustar a variável SOURCE_DIR no início do script para apontar para o caminho correto.${NC}"
  
  # Criar guia de migração mesmo sem o diretório de origem
  echo -e "${GREEN}Criando guia de migração de código-fonte...${NC}"
else
  echo -e "${GREEN}Diretório de origem encontrado: $SOURCE_DIR${NC}"
  echo -e "${GREEN}Criando guia de migração de código-fonte...${NC}"
fi

# Criar guia de migração de código-fonte
cat << 'END_MIGRATION_GUIDE' > $TEMP_DIR/source-code-migration-guide.md
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
END_MIGRATION_GUIDE

# Criar script auxiliar para ajustar imports
cat << 'END_IMPORT_HELPER' > $TEMP_DIR/scripts/adjust-imports.js
#!/usr/bin/env node

/**
 * Script para ajudar a modificar importações no código-fonte
 * durante a migração de React Native para Expo.
 * 
 * Uso: node adjust-imports.js <caminho_do_arquivo>
 */

const fs = require('fs');
const path = require('path');

// Mapeamento de módulos React Native para equivalentes do Expo
const moduleMapping = {
  'react-native-camera': 'expo-camera',
  'react-native-maps': 'react-native-maps', // Suportado pelo Expo
  'react-native-fs': 'expo-file-system',
  'react-native-device-info': 'expo-device',
  'react-native-permissions': 'expo-permissions',
  'react-native-push-notification': 'expo-notifications',
  'react-native-geolocation-service': 'expo-location',
  'react-native-image-picker': 'expo-image-picker',
  'react-native-video': 'expo-av',
  'react-native-sqlite-storage': 'expo-sqlite',
  'react-native-share': 'expo-sharing',
  'react-native-fbsdk': 'expo-facebook',
  'react-native-google-signin': 'expo-auth-session',
  'react-native-biometrics': 'expo-local-authentication',
  'react-native-keychain': 'expo-secure-store',
  '@react-native-community/netinfo': 'expo-network',
  'react-native-splash-screen': 'expo-splash-screen',
};

// Função principal
function adjustImports(filePath) {
  if (!fs.existsSync(filePath)) {
    console.error(`Arquivo não encontrado: ${filePath}`);
    process.exit(1);
  }

  // Ler o conteúdo do arquivo
  let content = fs.readFileSync(filePath, 'utf8');
  let originalContent = content;
  let changes = [];

  // Substituir módulos com base no mapeamento
  for (const [oldModule, newModule] of Object.entries(moduleMapping)) {
    const regex = new RegExp(`from\\s+['"]${oldModule}(['"/])`, 'g');
    if (regex.test(content)) {
      changes.push(`${oldModule} -> ${newModule}`);
    }
    content = content.replace(regex, `from '${newModule}$1`);
  }

  // Se houver mudanças, salvar o arquivo e mostrar as alterações
  if (content !== originalContent) {
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`\x1b[32mArquivo atualizado: ${filePath}\x1b[0m`);
    console.log('Alterações realizadas:');
    changes.forEach(change => console.log(`- ${change}`));
  } else {
    console.log(`\x1b[33mNenhuma alteração necessária em: ${filePath}\x1b[0m`);
  }
}

// Verificar argumentos da linha de comando
if (process.argv.length < 3) {
  console.log('Uso: node adjust-imports.js <caminho_do_arquivo>');
  process.exit(0);
}

// Executar para o arquivo fornecido
adjustImports(process.argv[2]);
END_IMPORT_HELPER

chmod +x $TEMP_DIR/scripts/adjust-imports.js

# Criar script de ajuste em lote
cat << 'END_BATCH_IMPORT_HELPER' > $TEMP_DIR/scripts/adjust-imports-batch.js
#!/usr/bin/env node

/**
 * Script para ajustar importações em vários arquivos
 * durante a migração de React Native para Expo.
 * 
 * Uso: node adjust-imports-batch.js <diretório>
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// Mapeamento de módulos React Native para equivalentes do Expo
const moduleMapping = {
  'react-native-camera': 'expo-camera',
  'react-native-maps': 'react-native-maps', // Suportado pelo Expo
  'react-native-fs': 'expo-file-system',
  'react-native-device-info': 'expo-device',
  'react-native-permissions': 'expo-permissions',
  'react-native-push-notification': 'expo-notifications',
  'react-native-geolocation-service': 'expo-location',
  'react-native-image-picker': 'expo-image-picker',
  'react-native-video': 'expo-av',
  'react-native-sqlite-storage': 'expo-sqlite',
  'react-native-share': 'expo-sharing',
  'react-native-fbsdk': 'expo-facebook',
  'react-native-google-signin': 'expo-auth-session',
  'react-native-biometrics': 'expo-local-authentication',
  'react-native-keychain': 'expo-secure-store',
  '@react-native-community/netinfo': 'expo-network',
  'react-native-splash-screen': 'expo-splash-screen',
};

// Extensões de arquivo a processar
const extensions = ['.js', '.jsx', '.ts', '.tsx'];

// Função para processar recursivamente um diretório
function processDirectory(directory) {
  const files = fs.readdirSync(directory);
  
  for (const file of files) {
    const fullPath = path.join(directory, file);
    const stat = fs.statSync(fullPath);
    
    if (stat.isDirectory()) {
      // Recursivamente processar subdiretórios
      processDirectory(fullPath);
    } else if (extensions.includes(path.extname(file))) {
      // Processar arquivos com extensões relevantes
      processFile(fullPath);
    }
  }
}

// Função para processar um arquivo
function processFile(filePath) {
  try {
    let content = fs.readFileSync(filePath, 'utf8');
    let originalContent = content;
    let changes = [];

    // Substituir módulos com base no mapeamento
    for (const [oldModule, newModule] of Object.entries(moduleMapping)) {
      const regex = new RegExp(`from\\s+['"]${oldModule}(['"/])`, 'g');
      if (regex.test(content)) {
        changes.push(`${oldModule} -> ${newModule}`);
      }
      content = content.replace(regex, `from '${newModule}$1`);
    }

    // Se houver mudanças, salvar o arquivo e mostrar as alterações
    if (content !== originalContent) {
      fs.writeFileSync(filePath, content, 'utf8');
      console.log(`\x1b[32mArquivo atualizado: ${filePath}\x1b[0m`);
      console.log('Alterações realizadas:');
      changes.forEach(change => console.log(`- ${change}`));
      console.log('---');
    }
  } catch (error) {
    console.error(`\x1b[31mErro ao processar ${filePath}: ${error.message}\x1b[0m`);
  }
}

// Verificar argumentos da linha de comando
if (process.argv.length < 3) {
  console.log('Uso: node adjust-imports-batch.js <diretório>');
  process.exit(0);
}

const targetDir = process.argv[2];

if (!fs.existsSync(targetDir)) {
  console.error(`Diretório não encontrado: ${targetDir}`);
  process.exit(1);
}

console.log(`\x1b[36mProcessando arquivos em: ${targetDir}\x1b[0m`);
processDirectory(targetDir);
console.log(`\x1b[36mProcessamento concluído.\x1b[0m`);
END_BATCH_IMPORT_HELPER

chmod +x $TEMP_DIR/scripts/adjust-imports-batch.js

echo -e "${GREEN}Guia de migração de código-fonte e scripts auxiliares criados com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para finalizar a migração${NC}"

cd ..
