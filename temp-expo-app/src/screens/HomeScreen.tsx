import ImageExample from '../components/ImageExample';
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Button from '../components/Button';
import { colors, spacing } from '../config/theme';

const HomeScreen = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Tela Inicial</Text>
        <ImageExample />
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
