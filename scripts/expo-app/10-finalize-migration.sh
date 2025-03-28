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

echo -e "${GREEN}Finalizando a migração para Expo...${NC}"

# Criar arquivo de README com instruções
cat << 'END_README' > $TEMP_DIR/README.md
# App Expo Migrado

Este projeto foi migrado de React Native puro para Expo.

## Estrutura do Projeto

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
├── scripts/            # Scripts auxiliares
└── __tests__/          # Testes
```

## Começando

1. Instale as dependências:
```bash
pnpm install
```

2. Inicie o servidor de desenvolvimento:
```bash
npm start
```

3. Execute em um dispositivo ou emulador:
- Pressione `a` para executar no emulador Android
- Pressione `i` para executar no simulador iOS
- Escaneie o QR code com o aplicativo Expo Go (Android) ou Câmera (iOS)

## Scripts Disponíveis

- `npm start` - Inicia o servidor de desenvolvimento
- `npm test` - Executa os testes
- `npm run eas-build:dev` - Cria uma build de desenvolvimento usando EAS
- `npm run eas-build:preview` - Cria uma build de preview usando EAS
- `npm run eas-build:prod` - Cria uma build de produção usando EAS

## Migração

Este projeto foi migrado de React Native puro para Expo. As seguintes alterações foram feitas:

1. Substituição de módulos nativos por equivalentes do Expo
2. Configuração de plugins do Expo no app.json
3. Adaptação da estrutura de diretórios
4. Configuração de testes com jest-expo
5. Configuração de EAS para build e deploy

## Guias de Migração

Guias detalhados sobre aspectos específicos da migração estão disponíveis em:

- `source-code-migration-guide.md` - Informações sobre a migração do código-fonte
- `native-modules-migration-guide.md` - Guia para módulos nativos
- `testing-migration-guide.md` - Guia para migrar e configurar testes

## Recursos Adicionais

- [Documentação do Expo](https://docs.expo.dev/)
- [Documentação do EAS](https://docs.expo.dev/eas/)
- [Guia de migração oficial](https://docs.expo.dev/workflow/upgrading/)
END_README

# Criar checklist de pós-migração
cat << 'END_CHECKLIST' > $TEMP_DIR/POST_MIGRATION_CHECKLIST.md
# Checklist Pós-Migração

Use este checklist para garantir que sua migração do React Native para o Expo esteja completa e funcionando corretamente.

## Ambiente de Desenvolvimento

- [ ] Verifique se o servidor de desenvolvimento inicia sem erros: `npm start`
- [ ] Confirme se o aplicativo é executado no iOS
- [ ] Confirme se o aplicativo é executado no Android
- [ ] Verifique se o Hot Reload está funcionando

## Funcionalidades

- [ ] Navegação - Todas as rotas funcionam
- [ ] Formulários - Campos e validações funcionam
- [ ] APIs - Chamadas de API funcionam corretamente
- [ ] Armazenamento local - Dados são salvos e recuperados corretamente
- [ ] Permissões - Solicitações de permissão funcionam
- [ ] Recursos específicos de plataforma - Funcionam em ambos iOS e Android

## Módulos Nativos

- [ ] Substitua todos os módulos nativos não suportados por equivalentes do Expo
- [ ] Configure os plugins necessários no app.json
- [ ] Verifique se cada funcionalidade nativa está operando corretamente

## UI e Experiência do Usuário

- [ ] As fontes estão carregando corretamente
- [ ] Imagens e ícones são exibidos corretamente
- [ ] Gestos e animações funcionam como esperado
- [ ] A navegação possui transições suaves
- [ ] A responsividade em diferentes tamanhos de tela

## Desempenho

- [ ] O tempo de inicialização é aceitável
- [ ] As animações são suaves
- [ ] O uso de memória é razoável
- [ ] O consumo de bateria não é excessivo

## Testes

- [ ] Testes unitários executam sem erros: `npm test`
- [ ] Snapshots estão atualizados
- [ ] Cobertura de testes é satisfatória

## Construção (Build)

- [ ] Configuração do EAS está correta
- [ ] Build de desenvolvimento funciona: `npm run eas-build:dev`
- [ ] Build de preview funciona: `npm run eas-build:preview` 
- [ ] Build de produção funciona: `npm run eas-build:prod`

## Próximos Passos

- [ ] Atualize as informações no app.json (nome, versão, ID)
- [ ] Configure atualizações OTA com o EAS Update
- [ ] Configure notificações push
- [ ] Configure análise de crashes
- [ ] Configure o GitHub Actions para CI/CD
END_CHECKLIST

# Criar script de verificação do ambiente
cat << 'END_ENV_CHECK' > $TEMP_DIR/scripts/check-environment.js
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
END_ENV_CHECK

chmod +x $TEMP_DIR/scripts/check-environment.js

# Criar documentação sobre as diferenças entre React Native e Expo
cat << 'END_DIFFERENCES_DOC' > $TEMP_DIR/REACT_NATIVE_VS_EXPO.md
# React Native vs Expo

## Principais Diferenças

| Aspecto | React Native Puro | Expo |
|---------|-------------------|------|
| **Setup inicial** | Mais complexo, requer configuração manual | Mais simples, configuração automatizada |
| **Código nativo** | Acesso total ao código nativo | Acesso limitado (com EAS, acesso via plugins) |
| **Dependências nativas** | Liberdade para usar qualquer biblioteca | Limitado às bibliotecas suportadas pelo Expo |
| **Compilação** | Compilação local usando Xcode/Android Studio | Compilação na nuvem usando EAS Build |
| **OTA Updates** | Requer solução personalizada | Integrado via EAS Update |
| **Configuração** | Arquivos de configuração separados por plataforma | Configuração centralizada via app.json |
| **Desenvolvimento** | Requer mais conhecimento de iOS/Android | Menos conhecimento nativo necessário |

## Vantagens do Expo

1. **Velocidade de desenvolvimento**: Setup mais rápido e iteração mais ágil
2. **Atualizações Over-the-Air**: Atualizações sem passar pelas lojas
3. **EAS Build**: Builds na nuvem sem necessidade de configurar ambiente local
4. **Simplicidade**: Menos código de configuração, mais foco na aplicação
5. **Manutenção**: Atualizações de SDK mais simples e confiáveis
6. **SDK unificado**: Módulos nativos pré-configurados

## Limitações do Expo

1. **Acesso nativo limitado**: Mesmo com os plugins, algumas funcionalidades nativas exigem workarounds
2. **Tamanho do aplicativo**: Apps Expo tendem a ser maiores devido às bibliotecas incluídas
3. **Controle reduzido**: Menos flexibilidade em algumas configurações nativas
4. **Dependência da infraestrutura Expo**: Para builds e updates
5. **Possíveis atrasos em novas APIs nativas**: Tempo para incorporar novos recursos do iOS/Android

## Quando usar Expo

- Startups e MVPs que precisam de desenvolvimento rápido
- Equipes com menos experiência em desenvolvimento móvel nativo
- Aplicativos que não precisam de integrações nativas muito específicas
- Quando a velocidade de atualização é crucial (OTA updates)

## Quando usar React Native puro

- Aplicativos que precisam de acesso a APIs nativas não suportadas pelo Expo
- Projetos que requerem otimizações específicas de performance
- Quando é necessário controle total sobre o código nativo
- Integração com SDKs nativos de terceiros não suportados pelo Expo

## O melhor dos dois mundos: Expo com EAS

Com o Expo Application Services (EAS), muitas das limitações tradicionais do Expo foram reduzidas:

- **EAS Build**: Permite configurações nativas mais avançadas
- **Config Plugins**: Permite modificar código nativo sem ejetar
- **Desenvolvimento com cliente personalizado**: Para testar módulos nativos personalizados
END_DIFFERENCES_DOC

echo -e "${GREEN}Documentação final e scripts de verificação criados com sucesso!${NC}"
# fix() correção falha chmod: cannot access 'scripts/expo-app/10-finalize-migration.sh': No such file or directory 
# cd ..
pwd
chmod +x scripts/expo-app/10-finalize-migration.sh
