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

echo -e "${GREEN}Configurando navegação para o projeto Expo...${NC}"

# Instalar dependências de navegação
cd $TEMP_DIR
echo -e "${GREEN}Instalando React Navigation e dependências...${NC}"
pnpm install @react-navigation/native @react-navigation/stack @react-navigation/bottom-tabs @react-navigation/drawer

# Criar telas básicas de exemplo
echo -e "${GREEN}Criando telas de exemplo...${NC}"

# Tela Home
cat << 'END_HOME' > src/screens/HomeScreen.tsx
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Button from '../components/Button';
import { colors, spacing } from '../config/theme';

const HomeScreen = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Tela Inicial</Text>
      <View style={styles.buttonContainer}>
        <Button
          title="Ir para Detalhes"
          onPress={() => navigation.navigate('Details')}
          style={styles.button}
        />
        <Button
          title="Abrir Perfil"
          variant="secondary"
          onPress={() => navigation.navigate('Profile')}
          style={styles.button}
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
    backgroundColor: colors.background,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: spacing.lg,
    textAlign: 'center',
  },
  buttonContainer: {
    gap: spacing.md,
  },
  button: {
    marginBottom: spacing.sm,
  },
});

export default HomeScreen;
END_HOME

# Tela Details
cat << 'END_DETAILS' > src/screens/DetailsScreen.tsx
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Button from '../components/Button';
import { colors, spacing } from '../config/theme';

const DetailsScreen = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Tela de Detalhes</Text>
      <Text style={styles.description}>
        Esta é uma tela de exemplo para demonstrar a navegação no Expo.
      </Text>
      <Button
        title="Voltar"
        variant="outline"
        onPress={() => navigation.goBack()}
        style={styles.button}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
    backgroundColor: colors.background,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: spacing.md,
    textAlign: 'center',
  },
  description: {
    fontSize: 16,
    marginBottom: spacing.lg,
    textAlign: 'center',
  },
  button: {
    alignSelf: 'center',
    width: 200,
  },
});

export default DetailsScreen;
END_DETAILS

# Tela Profile
cat << 'END_PROFILE' > src/screens/ProfileScreen.tsx
import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Button from '../components/Button';
import { colors, spacing } from '../config/theme';

const ProfileScreen = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.profileContainer}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>👤</Text>
        </View>
        <Text style={styles.name}>Nome do Usuário</Text>
        <Text style={styles.email}>usuario@example.com</Text>
      </View>
      
      <View style={styles.infoContainer}>
        <Text style={styles.infoTitle}>Informações do Perfil</Text>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Conta criada em:</Text>
          <Text style={styles.infoValue}>01/01/2023</Text>
        </View>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Último acesso:</Text>
          <Text style={styles.infoValue}>Hoje</Text>
        </View>
      </View>
      
      <Button
        title="Voltar para Home"
        onPress={() => navigation.navigate('Home')}
        style={styles.button}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
    backgroundColor: colors.background,
  },
  profileContainer: {
    alignItems: 'center',
    marginBottom: spacing.xl,
  },
  avatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: colors.secondary,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  avatarText: {
    fontSize: 40,
  },
  name: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: spacing.xs,
  },
  email: {
    fontSize: 16,
    color: '#666',
  },
  infoContainer: {
    backgroundColor: '#f5f5f5',
    borderRadius: 10,
    padding: spacing.md,
    marginBottom: spacing.xl,
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: spacing.md,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: spacing.sm,
  },
  infoLabel: {
    fontSize: 16,
    color: '#555',
  },
  infoValue: {
    fontSize: 16,
  },
  button: {
    alignSelf: 'center',
    width: 200,
  },
});

export default ProfileScreen;
END_PROFILE

# Configurar navegação
echo -e "${GREEN}Configurando arquivos de navegação...${NC}"

# Navegação Stack
cat << 'END_STACK_NAV' > src/navigation/StackNavigator.tsx
import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import HomeScreen from '../screens/HomeScreen';
import DetailsScreen from '../screens/DetailsScreen';
import ProfileScreen from '../screens/ProfileScreen';

const Stack = createStackNavigator();

const StackNavigator = () => {
  return (
    <Stack.Navigator
      initialRouteName="Home"
      screenOptions={{
        headerStyle: {
          backgroundColor: '#f4511e',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      }}
    >
      <Stack.Screen
        name="Home"
        component={HomeScreen}
        options={{ title: 'Início' }}
      />
      <Stack.Screen
        name="Details"
        component={DetailsScreen}
        options={{ title: 'Detalhes' }}
      />
      <Stack.Screen
        name="Profile"
        component={ProfileScreen}
        options={{ title: 'Perfil' }}
      />
    </Stack.Navigator>
  );
};

export default StackNavigator;
END_STACK_NAV

# Atualizar App.tsx para usar a navegação
cat << 'END_APP_NAV' > App.tsx
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import StackNavigator from './src/navigation/StackNavigator';

export default function App() {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <StackNavigator />
        <StatusBar style="auto" />
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
END_APP_NAV

cd ..
echo -e "${GREEN}Navegação configurada com sucesso!${NC}"
echo -e "${YELLOW}Execute o próximo script para configurar os assets e fontes${NC}"

chmod +x scripts/expo-app/03-setup-navigation.sh
