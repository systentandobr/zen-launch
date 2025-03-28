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
