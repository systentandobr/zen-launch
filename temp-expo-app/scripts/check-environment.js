#!/usr/bin/env node

/**
 * Script para verificar o ambiente de desenvolvimento do Expo
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

console.log('\x1b[36m=== Verificação de Ambiente Expo ===\x1b[0m\n');

// Função para executar comandos e capturar a saída
function runCommand(command) {
  try {
    return execSync(command, { encoding: 'utf8', stdio: 'pipe' });
  } catch (error) {
    return null;
  }
}

// Verificar versões
console.log('\x1b[33mVersões instaladas:\x1b[0m');

const nodeVersion = process.version;
console.log(`Node.js: ${nodeVersion}`);

const npmVersion = runCommand('npm --version');
console.log(`npm: ${npmVersion ? npmVersion.trim() : 'Não encontrado'}`);

const expoCliVersion = runCommand('expo --version');
console.log(`Expo CLI: ${expoCliVersion ? expoCliVersion.trim() : 'Não encontrado'}`);

const easCliVersion = runCommand('eas --version');
console.log(`EAS CLI: ${easCliVersion ? easCliVersion.trim() : 'Não encontrado'}`);

// Verificar dependências no package.json
console.log('\n\x1b[33mVerificando dependências no package.json:\x1b[0m');

try {
  const packageJsonPath = path.join(process.cwd(), 'package.json');
  const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
  
  const expoVersion = packageJson.dependencies.expo;
  console.log(`Expo SDK: ${expoVersion || 'Não encontrado'}`);
  
  const reactNativeVersion = packageJson.dependencies['react-native'];
  console.log(`React Native: ${reactNativeVersion || 'Não encontrado'}`);
  
  // Verificar plugins configurados no app.json
  console.log('\n\x1b[33mVerificando configuração do app.json:\x1b[0m');
  
  const appJsonPath = path.join(process.cwd(), 'app.json');
  if (fs.existsSync(appJsonPath)) {
    const appJson = JSON.parse(fs.readFileSync(appJsonPath, 'utf8'));
    const plugins = appJson.expo.plugins || [];
    
    console.log(`Número de plugins configurados: ${Array.isArray(plugins) ? plugins.length : 'N/A'}`);
    
    // Verificar configurações básicas
    console.log(`Nome do app: ${appJson.expo.name || 'Não definido'}`);
    console.log(`Slug: ${appJson.expo.slug || 'Não definido'}`);
    console.log(`Versão: ${appJson.expo.version || 'Não definida'}`);
    
    if (appJson.expo.ios) {
      console.log(`iOS Bundle Identifier: ${appJson.expo.ios.bundleIdentifier || 'Não definido'}`);
    }
    
    if (appJson.expo.android) {
      console.log(`Android Package: ${appJson.expo.android.package || 'Não definido'}`);
    }
  } else {
    console.log('app.json não encontrado!');
  }
  
} catch (error) {
  console.error('\x1b[31mErro ao ler package.json:\x1b[0m', error.message);
}

// Verificar se as ferramentas básicas estão disponíveis
console.log('\n\x1b[33mVerificando ferramentas de desenvolvimento:\x1b[0m');

// Verificar emuladores/simuladores disponíveis
console.log('\n\x1b[33mVerificando emuladores/simuladores disponíveis:\x1b[0m');

const androidEmulators = runCommand('emulator -list-avds');
if (androidEmulators) {
  console.log('Emuladores Android disponíveis:');
  androidEmulators.split('\n').filter(Boolean).forEach(emulator => {
    console.log(`- ${emulator}`);
  });
} else {
  console.log('Nenhum emulador Android encontrado ou emulator não está no PATH');
}

const xcrunOutput = runCommand('xcrun simctl list devices available');
if (xcrunOutput) {
  console.log('\nSimuladores iOS disponíveis (até 5 primeiros):');
  const iosDevices = xcrunOutput.match(/^    .+ \(.+\) \(.*\)$/gm);
  if (iosDevices) {
    iosDevices.slice(0, 5).forEach(device => {
      console.log(`- ${device.trim()}`);
    });
    if (iosDevices.length > 5) {
      console.log(`  ... e mais ${iosDevices.length - 5} dispositivos`);
    }
  } else {
    console.log('Nenhum simulador iOS encontrado');
  }
} else {
  console.log('Nenhum simulador iOS encontrado ou xcrun não está disponível (apenas macOS)');
}

console.log('\n\x1b[32mVerificação de ambiente concluída!\x1b[0m');
