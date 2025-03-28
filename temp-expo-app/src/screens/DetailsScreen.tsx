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
