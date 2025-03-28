#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Iniciando criação do projeto Expo...${NC}"

# Nome do aplicativo
read -p "Digite o nome do seu aplicativo: " APP_NAME

# Verifica se o nome foi fornecido
if [ -z "$APP_NAME" ]; then
  echo -e "${YELLOW}Nome não fornecido, usando 'my-expo-app' como padrão${NC}"
  APP_NAME="my-expo-app"
fi

# Criar diretório temporário para o novo projeto
TEMP_DIR="temp-expo-app"

if [ -d $TEMP_DIR ]; then
    echo "O diretório existe"

else
  echo "O diretório não existe"  
  mkdir -p $TEMP_DIR

  # Inicializar projeto Expo com TypeScript
  echo -e "${GREEN}Criando projeto Expo com TypeScript...${NC}"
  npx create-expo-app -t expo-template-blank-typescript $TEMP_DIR

  # Instalar dependências básicas
  cd $TEMP_DIR
  echo -e "${GREEN}Instalando dependências básicas...${NC}"
  pnpm install expo-font @expo/vector-icons react-native-screens react-native-safe-area-context

  # Voltar para diretório raiz
  cd ..

fi


echo -e "${GREEN}Projeto Expo criado com sucesso em $TEMP_DIR${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar a estrutura de pastas${NC}"

chmod +x scripts/expo-app/01-create-expo-app.sh
