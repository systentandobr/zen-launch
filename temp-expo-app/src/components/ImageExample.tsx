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
