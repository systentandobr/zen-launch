#!/bin/bash

# Script 25 criado automaticamente
echo "Executando claude-ai25.sh"
cat << 'EOF' > src/modules/weather/hooks/useWeather.ts
import { useState, useEffect } from 'react';
import { useAppSelector } from '@app/hooks';
import WeatherService from '@core/infrastructure/services/WeatherService';
import SystemSettingsModule from '@core/infrastructure/native/SystemSettingsModule';

interface WeatherData {
  city: string;
  temperature: number;
  condition: string;
  chanceOfRain: number;
  high: number;
  low: number;
  icon?: string;
  isLoading: boolean;
  error?: string;
}

export const useWeather = () => {
  const { showWeather, weatherLocation } = useAppSelector(state => state.settings);
  const [weatherData, setWeatherData] = useState<WeatherData>({
    city: '',
    temperature: 0,
    condition: '',
    chanceOfRain: 0,
    high: 0,
    low: 0,
    isLoading: true
  });
  const [isLocationEnabled, setIsLocationEnabled] = useState<boolean>(false);

  useEffect(() => {
    const checkLocationStatus = async () => {
      try {
        const enabled = await SystemSettingsModule.isLocationEnabled();
        setIsLocationEnabled(enabled);
      } catch (error) {
        console.error('Erro ao verificar status de localização:', error);
        setIsLocationEnabled(false);
      }
    };

    checkLocationStatus();
  }, []);

  useEffect(() => {
    if (!showWeather) return;

    const loadWeatherData = async () => {
      try {
        setWeatherData(prev => ({ ...prev, isLoading: true, error: undefined }));
        
        const data = await WeatherService.getWeatherData(weatherLocation || undefined);
        
        setWeatherData({
          ...data,
          isLoading: false
        });
      } catch (error) {
        setWeatherData(prev => ({ 
          ...prev, 
          isLoading: false, 
          error: error instanceof Error ? error.message : 'Erro ao carregar dados do clima'
        }));
      }
    };

    loadWeatherData();

    // Atualiza a cada hora
    const interval = setInterval(loadWeatherData, 60 * 60 * 1000);
    
    return () => clearInterval(interval);
  }, [showWeather, weatherLocation, isLocationEnabled]);

  const refreshWeather = async () => {
    try {
      setWeatherData(prev => ({ ...prev, isLoading: true, error: undefined }));
      
      const data = await WeatherService.getWeatherData(weatherLocation || undefined);
      
      setWeatherData({
        ...data,
        isLoading: false
      });
      
      return true;
    } catch (error) {
      setWeatherData(prev => ({ 
        ...prev, 
        isLoading: false, 
        error: error instanceof Error ? error.message : 'Erro ao carregar dados do clima'
      }));
      
      return false;
    }
  };

  const openLocationSettings = async () => {
    try {
      await SystemSettingsModule.openLocationSettings();
      return true;
    } catch (error) {
      console.error('Erro ao abrir configurações de localização:', error);
      return false;
    }
  };

  return {
    ...weatherData,
    isEnabled: showWeather,
    isLocationEnabled,
    refreshWeather,
    openLocationSettings
  };
};

export default useWeather;
EOF