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
