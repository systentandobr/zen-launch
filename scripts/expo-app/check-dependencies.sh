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

echo -e "${GREEN}Verificando dependências do projeto Expo...${NC}"

cd $TEMP_DIR

# Lista de dependências essenciais para Expo
ESSENTIAL_DEPS=(
  "expo"
  "expo-status-bar"
  "react"
  "react-native"
  "react-dom"
  "@react-navigation/native"
  "@react-navigation/stack"
  "expo-font"
  "expo-splash-screen"
  "expo-updates"
)

# Verificar se as dependências estão instaladas
echo -e "${BLUE}Verificando dependências essenciais...${NC}"
MISSING_DEPS=()

for dep in "${ESSENTIAL_DEPS[@]}"; do
  if ! grep -q "\"$dep\":" package.json; then
    MISSING_DEPS+=("$dep")
  fi
done

if [ ${#MISSING_DEPS[@]} -eq 0 ]; then
  echo -e "${GREEN}✅ Todas as dependências essenciais estão instaladas.${NC}"
else
  echo -e "${YELLOW}⚠️ Dependências essenciais faltando:${NC}"
  for dep in "${MISSING_DEPS[@]}"; do
    echo -e "   - $dep"
  done
  
  # Perguntar se deseja instalar as dependências faltantes
  read -p "Deseja instalar as dependências faltantes? (s/n): " INSTALL_DEPS
  
  if [[ $INSTALL_DEPS =~ ^[Ss]$ ]]; then
    echo -e "${GREEN}Instalando dependências faltantes...${NC}"
    pnpm install --save ${MISSING_DEPS[@]}
    echo -e "${GREEN}✅ Dependências instaladas com sucesso!${NC}"
  fi
fi

# Verificar configuração do app.json
echo -e "${BLUE}Verificando configuração do app.json...${NC}"

if [ ! -f "app.json" ]; then
  echo -e "${RED}❌ Arquivo app.json não encontrado!${NC}"
else
  # Verificar se as propriedades essenciais estão presentes
  MISSING_PROPS=()
  
  if ! grep -q "\"name\":" app.json; then
    MISSING_PROPS+=("name")
  fi
  
  if ! grep -q "\"slug\":" app.json; then
    MISSING_PROPS+=("slug")
  fi
  
  if ! grep -q "\"version\":" app.json; then
    MISSING_PROPS+=("version")
  fi
  
  if ! grep -q "\"orientation\":" app.json; then
    MISSING_PROPS+=("orientation")
  fi
  
  if ! grep -q "\"icon\":" app.json; then
    MISSING_PROPS+=("icon")
  fi
  
  if ! grep -q "\"splash\":" app.json; then
    MISSING_PROPS+=("splash")
  fi
  
  if [ ${#MISSING_PROPS[@]} -eq 0 ]; then
    echo -e "${GREEN}✅ Configuração do app.json está completa.${NC}"
  else
    echo -e "${YELLOW}⚠️ Propriedades faltando no app.json:${NC}"
    for prop in "${MISSING_PROPS[@]}"; do
      echo -e "   - $prop"
    done
  fi
fi

# Verificar estrutura de arquivos
echo -e "${BLUE}Verificando estrutura de arquivos...${NC}"

REQUIRED_DIRS=(
  "src/components"
  "src/screens"
  "src/navigation"
  "src/hooks"
  "src/config"
  "src/utils"
  "assets"
)

MISSING_DIRS=()

for dir in "${REQUIRED_DIRS[@]}"; do
  if [ ! -d "$dir" ]; then
    MISSING_DIRS+=("$dir")
  fi
done

if [ ${#MISSING_DIRS[@]} -eq 0 ]; then
  echo -e "${GREEN}✅ Estrutura de diretórios está completa.${NC}"
else
  echo -e "${YELLOW}⚠️ Diretórios faltando:${NC}"
  for dir in "${MISSING_DIRS[@]}"; do
    echo -e "   - $dir"
  done
  
  # Perguntar se deseja criar os diretórios faltantes
  read -p "Deseja criar os diretórios faltantes? (s/n): " CREATE_DIRS
  
  if [[ $CREATE_DIRS =~ ^[Ss]$ ]]; then
    echo -e "${GREEN}Criando diretórios faltantes...${NC}"
    for dir in "${MISSING_DIRS[@]}"; do
      mkdir -p "$dir"
    done
    echo -e "${GREEN}✅ Diretórios criados com sucesso!${NC}"
  fi
fi

echo -e "${GREEN}Verificação de dependências concluída!${NC}"

cd ..
