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
