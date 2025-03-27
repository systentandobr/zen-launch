#!/bin/bash

# Script 36 criado automaticamente
echo "Executando claude-ai36.sh"
cat << 'EOF' > src/modules/launcher/hooks/useAppCategories.ts
import { useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AppInfo } from '@state/slices/appsSlice';

const APP_CATEGORIES_STORAGE_KEY = '@MinimalLauncher:appCategories';

export const useAppCategories = (apps: AppInfo[]) => {
  const [appCategories, setAppCategories] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState<boolean>(true);
  
  const categories = [
    'Social',
    'Produtividade',
    'Jogos',
    'Mídia',
    'Utilidades',
    'Finanças',
    'Saúde',
    'Viagem',
    'Outros'
  ];

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setIsLoading(true);
      const stored = await AsyncStorage.getItem(APP_CATEGORIES_STORAGE_KEY);
      
      if (stored) {
        setAppCategories(JSON.parse(stored));
      } else {
        // Inicializa com categorias padrão para apps comuns
        const defaultCategories = getDefaultCategories();
        setAppCategories(defaultCategories);
        await AsyncStorage.setItem(APP_CATEGORIES_STORAGE_KEY, JSON.stringify(defaultCategories));
      }
    } catch (error) {
      console.error('Erro ao carregar categorias de apps:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const getDefaultCategories = (): Record<string, string> => {
    const defaults: Record<string, string> = {};
    
    // Mapeamento de pacotes comuns para categorias
    const categoryMap: Record<string, string[]> = {
      'Social': [
        'com.whatsapp',
        'com.instagram.android',
        'com.facebook.katana',
        'com.twitter.android',
        'com.snapchat.android',
        'com.linkedin.android',
        'com.pinterest'
      ],
      'Produtividade': [
        'com.google.android.gm',
        'com.google.android.calendar',
        'com.microsoft.office.outlook',
        'com.microsoft.office.word',
        'com.microsoft.office.excel',
        'com.google.android.keep',
        'com.evernote',
        'com.todoist'
      ],
      'Mídia': [
        'com.spotify.music',
        'com.google.android.youtube',
        'com.netflix.mediaclient',
        'com.amazon.avod.thirdpartyclient',
        'com.hbo.hbonow',
        'com.disney.disneyplus'
      ],
      'Jogos': [
        'com.supercell.clashofclans',
        'com.king.candycrushsaga',
        'com.rovio.angrybirds',
        'com.ea.game.',
        'com.nintendo.'
      ],
      'Utilidades': [
        'com.google.android.apps.maps',
        'com.android.chrome',
        'com.android.vending',
        'com.google.android.googlequicksearchbox',
        'com.google.android.apps.photos',
        'com.android.settings'
      ]
    };
    
    // Atribui categorias para apps instalados com base no padrão
    apps.forEach(app => {
      for (const [category, packagePrefixes] of Object.entries(categoryMap)) {
        if (packagePrefixes.some(prefix => app.packageName.startsWith(prefix))) {
          defaults[app.packageName] = category;
          break;
        }
      }
      
      // Se não encontrou categoria, atribui "Outros"
      if (!defaults[app.packageName]) {
        defaults[app.packageName] = 'Outros';
      }
    });
    
    return defaults;
  };

  const getAppCategory = (packageName: string): string => {
    return appCategories[packageName] || 'Outros';
  };

  const setAppCategory = async (packageName: string, category: string) => {
    try {
      const updated = { ...appCategories, [packageName]: category };
      setAppCategories(updated);
      await AsyncStorage.setItem(APP_CATEGORIES_STORAGE_KEY, JSON.stringify(updated));
      return true;
    } catch (error) {
      console.error('Erro ao salvar categoria do app:', error);
      return false;
    }
  };

  const getAppsByCategory = (category: string): AppInfo[] => {
    return apps.filter(app => getAppCategory(app.packageName) === category);
  };

  const resetCategories = async () => {
    try {
      const defaultCategories = getDefaultCategories();
      setAppCategories(defaultCategories);
      await AsyncStorage.setItem(APP_CATEGORIES_STORAGE_KEY, JSON.stringify(defaultCategories));
      return true;
    } catch (error) {
      console.error('Erro ao redefinir categorias:', error);
      return false;
    }
  };

  return {
    categories,
    appCategories,
    isLoading,
    getAppCategory,
    setAppCategory,
    getAppsByCategory,
    resetCategories
  };
};

export default useAppCategories;
EOF