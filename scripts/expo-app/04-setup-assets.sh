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

echo -e "${GREEN}Configurando assets e fontes para o projeto Expo...${NC}"

# Configurar fontes
cd $TEMP_DIR
echo -e "${GREEN}Configurando fontes...${NC}"

# Criar hook para carregamento de fontes
cat << 'END_FONT_HOOK' > src/hooks/useFonts.ts
import { useEffect, useState } from 'react';
import * as Font from 'expo-font';

export default function useFonts() {
  const [fontsLoaded, setFontsLoaded] = useState(false);

  useEffect(() => {
    async function loadFonts() {
      try {
        await Font.loadAsync({
          'Roboto-Regular': require('../assets/fonts/Roboto-Regular.ttf'),
          'Roboto-Bold': require('../assets/fonts/Roboto-Bold.ttf'),
          'Roboto-Medium': require('../assets/fonts/Roboto-Medium.ttf'),
        });
        setFontsLoaded(true);
      } catch (error) {
        console.error('Erro ao carregar fontes:', error);
        // Se houver erro, definimos como true para não bloquear o app
        setFontsLoaded(true);
      }
    }

    loadFonts();
  }, []);

  return fontsLoaded;
}
END_FONT_HOOK

# Criar diretório para fontes demo (para que o usuário possa substituir depois)
mkdir -p assets
mkdir -p assets/fonts

# Atualizar o App.tsx para usar o hook de fontes
cat << 'END_APP_FONTS' > App.tsx
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import { View, Text } from 'react-native';
import StackNavigator from './src/navigation/StackNavigator';
import useFonts from './src/hooks/useFonts';

export default function App() {
  const fontsLoaded = useFonts();

  if (!fontsLoaded) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>Carregando fontes...</Text>
      </View>
    );
  }

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <StackNavigator />
        <StatusBar style="auto" />
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
END_APP_FONTS

# Configurar app.json para assets
echo -e "${GREEN}Configurando app.json para assets...${NC}"
cat << 'END_APP_JSON' > app.json
{
  "expo": {
    "name": "ExpoApp",
    "slug": "expo-app",
    "version": "1.0.0",
    "orientation": "portrait",
    "icon": "./assets/icon.png",
    "userInterfaceStyle": "light",
    "splash": {
      "image": "./assets/splash.png",
      "resizeMode": "contain",
      "backgroundColor": "#ffffff"
    },
    "assetBundlePatterns": [
      "**/*"
    ],
    "ios": {
      "supportsTablet": true
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#ffffff"
      }
    },
    "web": {
      "favicon": "./assets/favicon.png"
    },
    "plugins": []
  }
}
END_APP_JSON

# Adicionar exemplo de uso de imagens
cat << 'END_IMAGE_COMPONENT' > src/components/ImageExample.tsx
import React from 'react';
import { View, Image, StyleSheet, Text } from 'react-native';
import { colors, spacing } from '../config/theme';

const ImageExample = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Exemplo de Imagem</Text>
      <Image
        source={require('../assets/images/placeholder.png')}
        style={styles.image}
        onError={(e) => console.log('Erro ao carregar imagem:', e.nativeEvent.error)}
        fallback={
          <View style={styles.fallback}>
            <Text>Imagem não encontrada</Text>
          </View>
        }
      />
      <Text style={styles.caption}>
        Substitua esta imagem por uma imagem real em src/assets/images/
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: spacing.md,
    alignItems: 'center',
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: spacing.sm,
  },
  image: {
    width: 200,
    height: 150,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
  },
  fallback: {
    width: 200,
    height: 150,
    backgroundColor: '#f0f0f0',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
  },
  caption: {
    fontSize: 12,
    color: '#666',
    marginTop: spacing.sm,
    textAlign: 'center',
  },
});

export default ImageExample;
END_IMAGE_COMPONENT

# Criar arquivo placeholder para imagens
mkdir -p src/assets/images
touch src/assets/images/placeholder.png

# Atualizar HomeScreen para incluir o componente de imagem
sed -i '' -e '/buttonContainer/i\
        <ImageExample \/>
' src/screens/HomeScreen.tsx || true

# Adicionar importação no topo do arquivo
sed -i '' -e '1,/from/s/from/from/;/from/i\
import ImageExample from '\''../components/ImageExample'\'';
' src/screens/HomeScreen.tsx || true

cd ..
echo -e "${GREEN}Assets e fontes configurados com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar o EAS Build${NC}"

chmod +x scripts/expo-app/04-setup-assets.sh
