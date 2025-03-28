#!/bin/bash

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===========================================${NC}"
echo -e "${BLUE}  PREPARANDO SCRIPTS DE MIGRAÇÃO          ${NC}"
echo -e "${BLUE}===========================================${NC}"

# Tornar todos os scripts executáveis
echo -e "${GREEN}Tornando todos os scripts executáveis...${NC}"
chmod +x scripts/expo-app/*.sh

# Verificar se há diretórios necessários
echo -e "${GREEN}Verificando diretórios necessários...${NC}"
[ -d "scripts/expo-app/scripts" ] || mkdir -p scripts/expo-app/scripts

# Criar diretório temporário para o projeto Expo se não existir
echo -e "${GREEN}Verificando diretório temporário para o projeto Expo...${NC}"
[ -d "temp-expo-app" ] || mkdir -p temp-expo-app
[ -d "temp-expo-app/scripts" ] || mkdir -p temp-expo-app/scripts

echo -e "${BLUE}===========================================${NC}"
echo -e "${GREEN}✅ Todos os scripts estão preparados para execução!${NC}"
echo -e "${YELLOW}Para iniciar a migração, use um dos comandos:${NC}"
echo -e "${BLUE}./scripts/expo-app/run-all.sh${NC} (migração básica)"
echo -e "${BLUE}./scripts/expo-app/run-all-updated.sh${NC} (com validação de ambiente)"
echo -e "${BLUE}===========================================${NC}"