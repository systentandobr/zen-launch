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
