#!/bin/bash

# Script 43 criado automaticamente
echo "Executando claude-ai43.sh"
cat << 'EOF' > src/modules/launcher/hooks/useLockScreen.ts
import { useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AppState, AppStateStatus } from 'react-native';

const LOCK_ENABLED_KEY = '@MinimalLauncher:lockEnabled';
const LOCK_TIMEOUT_KEY = '@MinimalLauncher:lockTimeoutMinutes';
const LAST_ACTIVE_KEY = '@MinimalLauncher:lastActiveTimestamp';

export const useLockScreen = () => {
  const [isLockEnabled, setIsLockEnabled] = useState<boolean>(false);
  const [lockTimeoutMinutes, setLockTimeoutMinutes] = useState<number>(5);
  const [isLocked, setIsLocked] = useState<boolean>(false);
  const [isInitialized, setIsInitialized] = useState<boolean>(false);

  // Carrega as configurações de bloqueio
  useEffect(() => {
    const loadLockSettings = async () => {
      try {
        const lockEnabledStr = await AsyncStorage.getItem(LOCK_ENABLED_KEY);
        const lockTimeoutStr = await AsyncStorage.getItem(LOCK_TIMEOUT_KEY);
        
        setIsLockEnabled(lockEnabledStr === 'true');
        
        if (lockTimeoutStr) {
          setLockTimeoutMinutes(parseInt(lockTimeoutStr, 10));
        }
        
        setIsInitialized(true);
      } catch (error) {
        console.error('Erro ao carregar configurações de bloqueio:', error);
        setIsInitialized(true);
      }
    };

    loadLockSettings();
  }, []);

  // Monitora o estado do app para bloquear quando fica inativo
  useEffect(() => {
    if (!isInitialized || !isLockEnabled) return;

    const handleAppStateChange = async (nextAppState: AppStateStatus) => {
      if (nextAppState === 'active') {
        // App voltou ao primeiro plano
        const lastActiveStr = await AsyncStorage.getItem(LAST_ACTIVE_KEY);
        
        if (lastActiveStr) {
          const lastActive = parseInt(lastActiveStr, 10);
          const now = Date.now();
          const timeDiffMinutes = (now - lastActive) / (1000 * 60);
          
          if (timeDiffMinutes >= lockTimeoutMinutes) {
            setIsLocked(true);
          }
        }
        
        // Atualiza o timestamp de último ativo
        await AsyncStorage.setItem(LAST_ACTIVE_KEY, Date.now().toString());
      } else if (nextAppState === 'background') {
        // App foi para o plano de fundo
        await AsyncStorage.setItem(LAST_ACTIVE_KEY, Date.now().toString());
      }
    };

    // Salva o timestamp inicial
    AsyncStorage.setItem(LAST_ACTIVE_KEY, Date.now().toString());

    const subscription = AppState.addEventListener('change', handleAppStateChange);

    return () => {
      subscription.remove();
    };
  }, [isInitialized, isLockEnabled, lockTimeoutMinutes]);

  // Salva as configurações quando mudarem
  useEffect(() => {
    if (!isInitialized) return;

    const saveLockSettings = async () => {
      try {
        await AsyncStorage.setItem(LOCK_ENABLED_KEY, isLockEnabled.toString());
        await AsyncStorage.setItem(LOCK_TIMEOUT_KEY, lockTimeoutMinutes.toString());
      } catch (error) {
        console.error('Erro ao salvar configurações de bloqueio:', error);
      }
    };

    saveLockSettings();
  }, [isInitialized, isLockEnabled, lockTimeoutMinutes]);

  const toggleLock = (enabled: boolean) => {
    setIsLockEnabled(enabled);
  };

  const setLockTimeout = (minutes: number) => {
    setLockTimeoutMinutes(minutes);
  };

  const unlock = () => {
    setIsLocked(false);
  };

  const lock = () => {
    if (isLockEnabled) {
      setIsLocked(true);
    }
  };

  return {
    isLockEnabled,
    lockTimeoutMinutes,
    isLocked,
    toggleLock,
    setLockTimeout,
    unlock,
    lock
  };
};

export default useLockScreen;
EOF