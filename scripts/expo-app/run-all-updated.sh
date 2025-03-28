#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===========================================${NC}"
echo -e "${BLUE}  MIGRAÇÃO DE REACT NATIVE PARA EXPO      ${NC}"
echo -e "${BLUE}===========================================${NC}"
echo -e "${GREEN}Iniciando processo de migração...${NC}"
echo ""

# Verificar ambiente de desenvolvimento primeiro
if [ -x "scripts/expo-app/validate-dev-env.sh" ]; then
  echo -e "${YELLOW}Validando ambiente de desenvolvimento...${NC}"
  ./scripts/expo-app/validate-dev-env.sh
fi

# Verificar se diretório de scripts existe
if [ ! -d "scripts/expo-app" ]; then
  echo -e "${RED}Diretório scripts/expo-app não encontrado.${NC}"
  echo -e "${RED}Certifique-se de estar executando este script a partir da raiz do projeto.${NC}"
  exit 1
fi

# Criar função para executar script
run_script() {
  SCRIPT=$1
  SCRIPT_NAME=$(basename "$SCRIPT")
  echo -e "${YELLOW}Executando: $SCRIPT_NAME${NC}"
  echo -e "${BLUE}------------------------------------------${NC}"
  if [ -x "$SCRIPT" ]; then
    $SCRIPT
  else
    chmod +x "$SCRIPT"
    $SCRIPT
  fi
  
  # Verificar resultado
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}Script $SCRIPT_NAME executado com sucesso!${NC}"
  else
    echo -e "${RED}Erro ao executar $SCRIPT_NAME.${NC}"
    exit 1
  fi
  echo ""
}

# Lista de scripts a serem executados em ordem
SCRIPTS=(
  "scripts/expo-app/01-create-expo-app.sh"
  "scripts/expo-app/02-setup-folder-structure.sh"
  "scripts/expo-app/03-setup-navigation.sh"
  "scripts/expo-app/04-setup-assets.sh"
  "scripts/expo-app/05-setup-eas.sh"
  "scripts/expo-app/06-setup-github-actions.sh"
  "scripts/expo-app/07-migrate-native-modules.sh"
  "scripts/expo-app/08-migrate-source-code.sh"
  "scripts/expo-app/09-setup-testing.sh"
  "scripts/expo-app/10-finalize-migration.sh"
  "scripts/expo-app/check-dependencies.sh"
)

# Executar cada script
for script in "${SCRIPTS[@]}"; do
  run_script "$script"
done

echo -e "${GREEN}Todos os scripts foram executados com sucesso!${NC}"
echo -e "${BLUE}===========================================${NC}"
echo -e "${YELLOW}Migração concluída!${NC}"
echo -e "${YELLOW}O projeto Expo está no diretório: temp-expo-app${NC}"
echo -e "${YELLOW}Você pode movê-lo para o diretório desejado e iniciar com:${NC}"
echo -e "${BLUE}cd temp-expo-app${NC}"
echo -e "${BLUE}npm start${NC}"
echo -e "${BLUE}===========================================${NC}"