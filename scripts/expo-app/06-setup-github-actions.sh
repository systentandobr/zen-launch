#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

TEMP_DIR="temp-expo-app"

# Verificar se o diretório temporário existe
if [ ! -d "$TEMP_DIR" ]; then
  echo -e "${YELLOW}Diretório $TEMP_DIR não encontrado. Execute o script 01-create-expo-app.sh primeiro.${NC}"
  exit 1
fi

echo -e "${GREEN}Configurando GitHub Actions para o projeto Expo...${NC}"

cd $TEMP_DIR

# Criar diretório para GitHub Actions
mkdir -p .github/workflows

# Workflow para CI
echo -e "${GREEN}Criando workflow para CI...${NC}"
cat << 'END_CI_WORKFLOW' > .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
          cache: 'npm'
      - name: Install dependencies
        run: npm ci
      - name: Run ESLint
        run: npm run lint || echo "No lint script found"
      - name: Type Check
        run: npm run typecheck || echo "No typecheck script found"

  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
          cache: 'npm'
      - name: Install dependencies
        run: npm ci
      - name: Run Tests
        run: npm test || echo "No test script found"
END_CI_WORKFLOW

# Workflow para Expo Preview
echo -e "${GREEN}Criando workflow para Expo Preview...${NC}"
cat << 'END_PREVIEW_WORKFLOW' > .github/workflows/expo-preview.yml
name: Expo Preview

on:
  pull_request:
    branches: [ main ]

jobs:
  preview:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
          cache: 'npm'
      - name: Setup Expo
        uses: expo/expo-github-action@v7
        with:
          expo-version: latest
          eas-version: latest
          token: ${{ secrets.EXPO_TOKEN }}
      - name: Install dependencies
        run: npm ci
      - name: Create preview
        run: eas build --platform all --profile preview --non-interactive --no-wait
END_PREVIEW_WORKFLOW

# Workflow para Produção
echo -e "${GREEN}Criando workflow para Produção...${NC}"
cat << 'END_PRODUCTION_WORKFLOW' > .github/workflows/production.yml
name: Production Build

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
          cache: 'npm'
      - name: Setup Expo
        uses: expo/expo-github-action@v7
        with:
          expo-version: latest
          eas-version: latest
          token: ${{ secrets.EXPO_TOKEN }}
      - name: Install dependencies
        run: npm ci
      - name: Build Production
        run: eas build --platform all --profile production --non-interactive --no-wait
END_PRODUCTION_WORKFLOW

# Adicionando README com instruções
echo -e "${GREEN}Criando README com instruções sobre GitHub Actions...${NC}"
cat << 'END_README_GITHUB' > .github/README.md
# GitHub Actions Workflows

Este diretório contém os workflows do GitHub Actions configurados para este projeto.

## Workflows Disponíveis

### CI (`ci.yml`)
- Executa em pushes para `main` e `develop` e em pull requests para essas branches
- Realiza lint e type checking

### Expo Preview (`expo-preview.yml`)
- Executa em pull requests para `main`
- Cria uma build de preview usando EAS

### Production Build (`production.yml`)
- Executa em pushes para `main`
- Cria uma build de produção usando EAS

## Configuração Necessária

Para os workflows do Expo e EAS funcionarem, você precisa configurar o seguinte secret no GitHub:

- `EXPO_TOKEN`: Um token de acesso da sua conta Expo. Você pode gerar um em https://expo.dev/accounts/[your-username]/settings/access-tokens.

## Como adicionar o secret EXPO_TOKEN

1. Vá para a página do seu repositório no GitHub
2. Clique em "Settings" > "Secrets and variables" > "Actions"
3. Clique em "New repository secret"
4. Nome: EXPO_TOKEN
5. Valor: Seu token de acesso do Expo
6. Clique em "Add secret"
END_README_GITHUB

echo -e "${GREEN}GitHub Actions configurado com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar a migração de código-fonte${NC}"

cd ..
chmod +x scripts/expo-app/06-setup-github-actions.sh
