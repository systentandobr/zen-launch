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
