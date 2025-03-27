#!/bin/bash

# Script 44 criado automaticamente
echo "Executando claude-ai44.sh"
cat << 'EOF' > src/modules/launcher/screens/AboutScreen.tsx
import React from 'react';
import { View, ScrollView, SafeAreaView, StatusBar, Image, Linking } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useTheme } from '@core/ui/theme/ThemeContext';
import Text from '@core/ui/components/Text';
import Button from '@core/ui/components/Button';
import Card from '@core/ui/components/Card';

const AboutScreen: React.FC = () => {
  const { isDark } = useTheme();
  const navigation = useNavigation();
  
  const appVersion = '1.0.0';
  
  const handleOpenGithub = () => {
    Linking.openURL('https://github.com/yourusername/zen-launcher');
  };
  
  const handleSendFeedback = () => {
    Linking.openURL('mailto:your.email@example.com?subject=Zen%20Launcher%20Feedback');
  };
  
  return (
    <SafeAreaView className={`flex-1 ${isDark ? 'bg-background-dark' : 'bg-background-light'}`}>
      <StatusBar 
        barStyle={isDark ? 'light-content' : 'dark-content'} 
        backgroundColor={isDark ? '#121212' : '#F9F9F9'} 
      />
      
      <ScrollView className="flex-1 px-4 pt-6">
        <View className="items-center mb-6">
          <View 
            className={`w-24 h-24 rounded-3xl items-center justify-center mb-4 ${
              isDark ? 'bg-gray-800' : 'bg-white'
            }`}
          >
            <Text className="text-5xl">🏠</Text>
          </View>
          
          <Text variant="h2">Zen Launcher</Text>
          <Text className="opacity-70 mt-1">Versão {appVersion}</Text>
        </View>
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-2">Sobre</Text>
          <Text className="mb-4">
            Zen Launcher é um aplicativo de launcher para Android projetado para melhorar seu foco
            e produtividade, eliminando distrações e fornecendo uma interface limpa e simples.
          </Text>
          
          <Text className="mb-4">
            Este launcher inclui recursos como modo de foco, rastreamento de uso de apps,
            gerenciamento de notificações e uma interface zenista.
          </Text>
        </Card>
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-2">Recursos</Text>
          
          <View className="mb-2">
            <Text className="font-medium">• Modo Foco</Text>
            <Text className="ml-4">Limite o uso de aplicativos distrativas durante períodos de foco</Text>
          </View>
          
          <View className="mb-2">
            <Text className="font-medium">• Rastreamento de Uso</Text>
            <Text className="ml-4">Acompanhe quanto tempo você gasta em cada aplicativo</Text>
          </View>
          
          <View className="mb-2">
            <Text className="font-medium">• Gerenciamento de Tarefas</Text>
            <Text className="ml-4">Lista de tarefas simples para organizar suas atividades</Text>
          </View>
          
          <View className="mb-2">
            <Text className="font-medium">• Design Minimalista</Text>
            <Text className="ml-4">Interface limpa para reduzir distrações visuais</Text>
          </View>
        </Card>
        
        <Card className="mb-4">
          <Text variant="h3" className="mb-2">Feedback</Text>
          <Text className="mb-4">
            Suas sugestões e feedback são importantes para melhorar este aplicativo.
            Por favor, entre em contato se encontrar qualquer bug ou tiver ideias para novos recursos.
          </Text>
          
          <Button
            onPress={handleSendFeedback}
            className="mb-2"
          >
            Enviar Feedback
          </Button>
          
          <Button
            variant="outline"
            onPress={handleOpenGithub}
          >
            Ver no GitHub
          </Button>
        </Card>
        
        <View className="items-center my-4">
          <Text className="opacity-50">Desenvolvido com 💖 em 2025</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default AboutScreen;
EOF