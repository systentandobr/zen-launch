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
