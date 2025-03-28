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

echo -e "${GREEN}Configurando EAS (Expo Application Services) para o projeto...${NC}"

# Instalar EAS CLI globalmente
echo -e "${GREEN}Instalando EAS CLI...${NC}"
# pnpm install -g eas-cli

cd $TEMP_DIR

# Criar arquivo de configuração eas.json
echo -e "${GREEN}Criando arquivo eas.json...${NC}"
cat << 'END_EAS_JSON' > eas.json
{
  "cli": {
    "version": ">= 3.15.0"
  },
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal",
      "ios": {
        "resourceClass": "m-medium"
      }
    },
    "preview": {
      "distribution": "internal",
      "ios": {
        "resourceClass": "m-medium"
      }
    },
    "production": {
      "ios": {
        "resourceClass": "m-medium"
      }
    }
  },
  "submit": {
    "production": {}
  }
}
END_EAS_JSON

# Adicionar script para EAS build no package.json
echo -e "${GREEN}Atualizando package.json com scripts para EAS...${NC}"
# Usar jq se disponível, caso contrário usar sed
if command -v jq &> /dev/null; then
  jq '.scripts += {
    "eas-build:dev": "eas build --profile development",
    "eas-build:preview": "eas build --profile preview",
    "eas-build:prod": "eas build --profile production"
  }' package.json > package.json.tmp && mv package.json.tmp package.json
else
  # Backup do package.json original
  cp package.json package.json.bak
  
  # Adicionar scripts usando sed
  sed -i '' -e 's/"scripts": {/"scripts": {\
    "eas-build:dev": "eas build --profile development",\
    "eas-build:preview": "eas build --profile preview",\
    "eas-build:prod": "eas build --profile production",/g' package.json || true
fi

echo -e "${GREEN}EAS configurado com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar o GitHub Actions${NC}"

cd ..
chmod +x scripts/expo-app/05-setup-eas.sh
