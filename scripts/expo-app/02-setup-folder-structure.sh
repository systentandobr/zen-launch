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

echo -e "${GREEN}Configurando estrutura de pastas para o projeto Expo...${NC}"

# Criar estrutura de pastas
cd $TEMP_DIR
mkdir -p src/components
mkdir -p src/screens
mkdir -p src/navigation
mkdir -p src/hooks
mkdir -p src/services
mkdir -p src/assets/images
mkdir -p src/assets/fonts
mkdir -p src/utils
mkdir -p src/contexts
mkdir -p src/config

# Criar arquivo de configuração básico
cat << 'END_CONFIG' > src/config/index.ts
export const APP_CONFIG = {
  apiUrl: __DEV__ ? 'http://localhost:3000' : 'https://api.example.com',
  version: '1.0.0',
};
END_CONFIG

# Criar arquivo de tema básico
cat << 'END_THEME' > src/config/theme.ts
export const colors = {
  primary: '#007AFF',
  secondary: '#5856D6',
  background: '#FFFFFF',
  text: '#000000',
  error: '#FF3B30',
  success: '#34C759',
  warning: '#FF9500',
  info: '#5AC8FA',
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
};

export const typography = {
  fontSizes: {
    xs: 12,
    sm: 14,
    md: 16,
    lg: 18,
    xl: 20,
    xxl: 24,
  },
  fontWeights: {
    regular: '400',
    medium: '500',
    bold: '700',
  },
};
END_THEME

# Criar componente Button básico
cat << 'END_BUTTON' > src/components/Button.tsx
import React from 'react';
import { TouchableOpacity, Text, StyleSheet, TouchableOpacityProps } from 'react-native';
import { colors, spacing, typography } from '../config/theme';

interface ButtonProps extends TouchableOpacityProps {
  title: string;
  variant?: 'primary' | 'secondary' | 'outline';
  size?: 'small' | 'medium' | 'large';
}

const Button: React.FC<ButtonProps> = ({
  title,
  variant = 'primary',
  size = 'medium',
  style,
  ...rest
}) => {
  return (
    <TouchableOpacity
      style={[
        styles.button,
        styles[variant],
        styles[size],
        style,
      ]}
      {...rest}
    >
      <Text style={[styles.text, variant === 'outline' && styles.outlineText]}>
        {title}
      </Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  primary: {
    backgroundColor: colors.primary,
  },
  secondary: {
    backgroundColor: colors.secondary,
  },
  outline: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: colors.primary,
  },
  small: {
    paddingVertical: spacing.xs,
    paddingHorizontal: spacing.sm,
  },
  medium: {
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
  },
  large: {
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.lg,
  },
  text: {
    color: 'white',
    fontSize: typography.fontSizes.md,
    fontWeight: typography.fontWeights.medium,
  },
  outlineText: {
    color: colors.primary,
  },
});

export default Button;
END_BUTTON

# Modificar App.tsx para usar a nova estrutura
cat << 'END_APP' > App.tsx
import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Button from './src/components/Button';
import { colors } from './src/config/theme';

export default function App() {
  return (
    <SafeAreaProvider>
      <View style={styles.container}>
        <Text style={styles.title}>Bem-vindo ao seu app Expo!</Text>
        <Text style={styles.subtitle}>
          Este é um projeto migrado de React Native puro para Expo
        </Text>
        <View style={styles.buttonContainer}>
          <Button
            title="Botão Primário"
            onPress={() => alert('Botão primário pressionado')}
          />
        </View>
        <StatusBar style="auto" />
      </View>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: colors.text,
  },
  subtitle: {
    fontSize: 16,
    marginBottom: 32,
    textAlign: 'center',
    color: colors.text,
  },
  buttonContainer: {
    width: '100%',
    maxWidth: 300,
  },
});
END_APP

cd ..
echo -e "${GREEN}Estrutura de pastas configurada com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar as dependências de navegação${NC}"

chmod +x scripts/expo-app/02-setup-folder-structure.sh
