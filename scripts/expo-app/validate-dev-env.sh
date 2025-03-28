#!/bin/bash
set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===========================================${NC}"
echo -e "${BLUE}  VALIDAÇÃO DE AMBIENTE PARA EXPO         ${NC}"
echo -e "${BLUE}===========================================${NC}"

# Verificar Node.js
echo -e "${BLUE}Verificando Node.js...${NC}"
if command -v node &> /dev/null; then
  NODE_VERSION=$(node -v)
  echo -e "${GREEN}✅ Node.js instalado: $NODE_VERSION${NC}"
  
  # Verificar se é uma versão compatível com Expo (>= 14.x)
  NODE_MAJOR_VERSION=$(echo $NODE_VERSION | cut -d. -f1 | sed 's/v//')
  if [ $NODE_MAJOR_VERSION -lt 14 ]; then
    echo -e "${YELLOW}⚠️ A versão do Node.js é antiga. O Expo recomenda Node.js 14.x ou superior.${NC}"
  fi
else
  echo -e "${RED}❌ Node.js não está instalado!${NC}"
  echo -e "${YELLOW}Por favor, instale Node.js 14.x ou superior: https://nodejs.org${NC}"
fi

# Verificar npm
echo -e "${BLUE}Verificando npm...${NC}"
if command -v npm &> /dev/null; then
  NPM_VERSION=$(npm -v)
  echo -e "${GREEN}✅ npm instalado: $NPM_VERSION${NC}"
else
  echo -e "${RED}❌ npm não está instalado!${NC}"
fi

# Verificar Expo CLI
echo -e "${BLUE}Verificando Expo CLI...${NC}"
if command -v expo &> /dev/null; then
  EXPO_VERSION=$(expo --version)
  echo -e "${GREEN}✅ Expo CLI instalado: $EXPO_VERSION${NC}"
else
  echo -e "${YELLOW}⚠️ Expo CLI não está instalado globalmente.${NC}"
  echo -e "${YELLOW}Instalando Expo CLI globalmente...${NC}"
  # npm install -g expo-cli
fi

# Verificar EAS CLI
echo -e "${BLUE}Verificando EAS CLI...${NC}"
if command -v eas &> /dev/null; then
  EAS_VERSION=$(eas --version)
  echo -e "${GREEN}✅ EAS CLI instalado: $EAS_VERSION${NC}"
else
  echo -e "${YELLOW}⚠️ EAS CLI não está instalado globalmente.${NC}"
  echo -e "${YELLOW}Instalando EAS CLI globalmente...${NC}"
  npm install -g eas-cli
fi

# Verificar Git
echo -e "${BLUE}Verificando Git...${NC}"
if command -v git &> /dev/null; then
  GIT_VERSION=$(git --version)
  echo -e "${GREEN}✅ Git instalado: $GIT_VERSION${NC}"
else
  echo -e "${RED}❌ Git não está instalado!${NC}"
  echo -e "${YELLOW}Por favor, instale Git: https://git-scm.com/downloads${NC}"
fi

# Verificar watchman (útil para desenvolvimento React Native)
echo -e "${BLUE}Verificando Watchman...${NC}"
if command -v watchman &> /dev/null; then
  WATCHMAN_VERSION=$(watchman --version)
  echo -e "${GREEN}✅ Watchman instalado: $WATCHMAN_VERSION${NC}"
else
  echo -e "${YELLOW}⚠️ Watchman não está instalado.${NC}"
  echo -e "${YELLOW}Embora opcional, Watchman é recomendado para melhor desempenho durante o desenvolvimento.${NC}"
  echo -e "${YELLOW}Mais informações: https://facebook.github.io/watchman/docs/install.html${NC}"
fi

# Verificar JDK (necessário para desenvolvimento Android)
echo -e "${BLUE}Verificando JDK...${NC}"
if command -v javac &> /dev/null; then
  JAVAC_VERSION=$(javac -version 2>&1)
  echo -e "${GREEN}✅ JDK instalado: $JAVAC_VERSION${NC}"
else
  echo -e "${YELLOW}⚠️ JDK não está instalado ou não está no PATH.${NC}"
  echo -e "${YELLOW}JDK é necessário para desenvolvimento Android.${NC}"
fi

# Verificar Android Studio tools (para desenvolvimento Android)
echo -e "${BLUE}Verificando Android SDK...${NC}"
if [ -d "$ANDROID_HOME" ] || [ -d "$ANDROID_SDK_ROOT" ]; then
  echo -e "${GREEN}✅ Android SDK encontrado${NC}"
else
  echo -e "${YELLOW}⚠️ Android SDK não encontrado no ambiente.${NC}"
  echo -e "${YELLOW}Se você planeja desenvolver para Android, instale o Android Studio.${NC}"
fi

# Verificar XCode (apenas em macOS, para desenvolvimento iOS)
if [[ "$OSTYPE" == "darwin"* ]]; then
  echo -e "${BLUE}Verificando XCode...${NC}"
  if command -v xcodebuild &> /dev/null; then
    XCODE_VERSION=$(xcodebuild -version | head -n 1)
    echo -e "${GREEN}✅ XCode instalado: $XCODE_VERSION${NC}"
  else
    echo -e "${YELLOW}⚠️ XCode não está instalado.${NC}"
    echo -e "${YELLOW}XCode é necessário para desenvolvimento iOS.${NC}"
  fi
fi

echo -e "${GREEN}Validação de ambiente concluída!${NC}"
echo -e "${BLUE}===========================================${NC}"
echo -e "${YELLOW}Nota: Para um ambiente de desenvolvimento completo para iOS e Android,${NC}"
echo -e "${YELLOW}consulte a documentação oficial do Expo: https://docs.expo.dev/get-started/installation/${NC}"
echo -e "${BLUE}===========================================${NC}"