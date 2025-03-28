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

echo -e "${GREEN}Configurando ambiente de testes para o projeto Expo...${NC}"

cd $TEMP_DIR

# Instalar dependências de teste
echo -e "${GREEN}Instalando dependências de teste...${NC}"
pnpm install --save-dev jest jest-expo react-test-renderer @testing-library/react-native @testing-library/jest-native

# Atualizar package.json para incluir script de teste
echo -e "${GREEN}Atualizando package.json com configurações de teste...${NC}"
# Backup do package.json original
cp package.json package.json.bak

# Usar jq se disponível, caso contrário usar sed
if command -v jq &> /dev/null; then
  jq '.scripts.test = "jest"' package.json > package.json.tmp && mv package.json.tmp package.json
  
  # Adicionar configuração do Jest
  jq '.jest = {
    "preset": "jest-expo",
    "transformIgnorePatterns": [
      "node_modules/(?!((jest-)?react-native|@react-native(-community)?)|expo(nent)?|@expo(nent)?/.*|@expo-google-fonts/.*|react-navigation|@react-navigation/.*|@unimodules/.*|unimodules|sentry-expo|native-base|react-native-svg)"
    ],
    "setupFilesAfterEnv": [
      "@testing-library/jest-native/extend-expect"
    ],
    "moduleFileExtensions": [
      "ts",
      "tsx",
      "js",
      "jsx"
    ],
    "collectCoverage": true,
    "collectCoverageFrom": [
      "**/*.{js,jsx,ts,tsx}",
      "!**/coverage/**",
      "!**/node_modules/**",
      "!**/babel.config.js",
      "!**/jest.setup.js"
    ]
  }' package.json > package.json.tmp && mv package.json.tmp package.json
else
  # Se jq não estiver disponível, notificar para instalação manual
  echo -e "${YELLOW}jq não está instalado. Atualizando package.json manualmente...${NC}"
  
  # Adicionar script de teste
  sed -i '' -e 's/"scripts": {/"scripts": {\
    "test": "jest",/g' package.json || true
  
  # Será necessário adicionar a configuração do Jest manualmente
  echo -e "${YELLOW}Por favor, adicione manualmente a configuração do Jest ao package.json.${NC}"
fi

# Criar diretório de testes
mkdir -p __tests__

# Criar arquivo de teste de exemplo para Button
cat << 'END_BUTTON_TEST' > __tests__/Button.test.tsx
import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import Button from '../src/components/Button';

describe('Button Component', () => {
  it('renders correctly with default props', () => {
    const { getByText } = render(<Button title="Test Button" />);
    expect(getByText('Test Button')).toBeTruthy();
  });

  it('calls onPress function when pressed', () => {
    const onPressMock = jest.fn();
    const { getByText } = render(
      <Button title="Pressable Button" onPress={onPressMock} />
    );

    fireEvent.press(getByText('Pressable Button'));
    expect(onPressMock).toHaveBeenCalledTimes(1);
  });

  it('applies correct style for primary variant', () => {
    const { getByText } = render(<Button title="Primary Button" variant="primary" />);
    const buttonText = getByText('Primary Button');
    
    // Verificar que o texto do botão primário tem estilo correto (cor branca)
    expect(buttonText.props.style).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ color: 'white' })
      ])
    );
  });

  it('applies correct style for outline variant', () => {
    const { getByText } = render(<Button title="Outline Button" variant="outline" />);
    const buttonText = getByText('Outline Button');
    
    // Verificar que o texto do botão outline tem o estilo correto
    expect(buttonText.props.style).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ color: expect.any(String) })
      ])
    );
  });
});
END_BUTTON_TEST

# Criar arquivo de setup de teste
cat << 'END_JEST_SETUP' > jest.setup.js
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
END_JEST_SETUP

# Criar guia para migração de testes
cat << 'END_TEST_MIGRATION_GUIDE' > testing-migration-guide.md
# Guia de Migração de Testes para Expo

## Configuração do Jest

A configuração do Jest para Expo já foi adicionada ao seu `package.json`. Elementos principais:

- `preset: "jest-expo"` - Preset específico para Expo
- `transformIgnorePatterns` - Configurados para lidar corretamente com módulos do Expo
- `setupFilesAfterEnv` - Adiciona suporte ao @testing-library/jest-native

## Migrando Testes Existentes

Ao migrar seus testes de React Native para Expo, siga estes passos:

1. **Atualizar Imports**:
   - Substitua imports de módulos React Native por equivalentes do Expo
   - Exemplo: `react-native-geolocation` → `expo-location`

2. **Mocks para Módulos Nativos**:
   - Use o arquivo `jest.setup.js` para configurar mocks para módulos nativos
   - Adicione mocks para módulos específicos do Expo que você utiliza

3. **Ajustar Snapshots**:
   - Snapshots precisarão ser atualizados devido às diferenças nos componentes
   - Execute `npm test -- -u` para atualizar snapshots após a migração

4. **Testes de Integração**:
   - Para testes que dependem de APIs nativas, use mocks mais complexos
   - Considere usar `jest.mock()` para simular o comportamento específico

## Exemplo de Migração

**Antes (React Native):**
```jsx
// Button.test.js
import React from 'react';
import { render } from '@testing-library/react-native';
import Button from '../src/components/Button';

test('renders correctly', () => {
  const { getByText } = render(<Button title="Test" />);
  expect(getByText('Test')).toBeTruthy();
});
```

**Depois (Expo):**
```jsx
// Button.test.tsx
import React from 'react';
import { render } from '@testing-library/react-native';
import Button from '../src/components/Button';

describe('Button Component', () => {
  it('renders correctly', () => {
    const { getByText } = render(<Button title="Test" />);
    expect(getByText('Test')).toBeTruthy();
  });
});
```

## Executando Testes

- Execute `npm test` para rodar todos os testes
- Execute `npm test -- --watch` para modo de observação
- Execute `npm test -- -u` para atualizar snapshots
- Execute `npm test -- --coverage` para relatório de cobertura

## Considerações para Componentes Específicos do Expo

Para componentes que usam recursos específicos do Expo, você precisará criar mocks mais elaborados:

```js
// Mock para expo-camera
jest.mock('expo-camera', () => ({
  Camera: jest.fn().mockImplementation(({ children }) => children),
  CameraType: {
    front: 'front',
    back: 'back'
  },
  FlashMode: {
    on: 'on',
    off: 'off'
  }
}));

// Mock para expo-location
jest.mock('expo-location', () => ({
  getCurrentPositionAsync: jest.fn().mockResolvedValue({
    coords: {
      latitude: 37.7749,
      longitude: -122.4194
    }
  }),
  requestForegroundPermissionsAsync: jest.fn().mockResolvedValue({ status: 'granted' })
}));
```
END_TEST_MIGRATION_GUIDE

echo -e "${GREEN}Ambiente de testes configurado com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para finalizar a configuração${NC}"

cd ..
pwd
chmod +x scripts/expo-app/09-setup-testing.sh
