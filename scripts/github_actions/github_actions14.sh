#!/bin/bash

# Script 14 criado automaticamente
echo "Executando claude-ai14.sh"
cat << 'EOF' > src/core/infrastructure/services/WeatherService.ts
/**
 * Serviço para obter dados meteorológicos
 */
import { Platform } from 'react-native';
import { SystemService } from '../native/AppModule';

// API key para o serviço de clima
const WEATHER_API_KEY = 'YOUR_WEATHER_API_KEY'; // Substituir pela chave real

export interface WeatherData {
  city: string;
  temperature: number;
  condition: string;
  chanceOfRain: number;
  high: number;
  low: number;
  icon?: string;
}

/**
 * Obtém as coordenadas de geolocalização
 */
const getCurrentPosition = (): Promise<{ latitude: number, longitude: number }> => {
  return new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(
      position => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        });
      },
      error => {
        console.error('Erro ao obter localização:', error);
        reject(error);
      },
      { enableHighAccuracy: false, timeout: 15000, maximumAge: 10000 }
    );
  });
};

/**
 * Obtém o nome da cidade com base nas coordenadas
 */
const getCityFromCoordinates = async (latitude: number, longitude: number): Promise<string> => {
  try {
    const response = await fetch(
      `https://api.openweathermap.org/geo/1.0/reverse?lat=${latitude}&lon=${longitude}&limit=1&appid=${WEATHER_API_KEY}`
    );
    const data = await response.json();
    
    if (data && data.length > 0) {
      return data[0].name;
    }
    
    return 'Desconhecido';
  } catch (error) {
    console.error('Erro ao obter cidade:', error);
    return 'Desconhecido';
  }
};

/**
 * Mapeia condições climáticas para textos em português
 */
const mapWeatherCondition = (condition: string): string => {
  const conditionMap: Record<string, string> = {
    'Clear': 'Céu Limpo',
    'Clouds': 'Nublado',
    'Rain': 'Chuva',
    'Drizzle': 'Garoa',
    'Thunderstorm': 'Tempestade',
    'Snow': 'Neve',
    'Mist': 'Névoa',
    'Smoke': 'Fumaça',
    'Haze': 'Neblina',
    'Dust': 'Poeira',
    'Fog': 'Nevoeiro',
    'Sand': 'Areia',
    'Ash': 'Cinzas',
    'Squall': 'Rajada',
    'Tornado': 'Tornado'
  };
  
  return conditionMap[condition] || condition;
};

/**
 * Serviço de clima
 */
const WeatherService = {
  /**
   * Obtém dados meteorológicos para uma localização
   */
  getWeatherData: async (location?: string): Promise<WeatherData> => {
    try {
      // Verificar se a localização está habilitada
      if (Platform.OS === 'android') {
        const isLocationEnabled = await SystemService.isLocationEnabled();
        if (!isLocationEnabled) {
          throw new Error('Localização desabilitada');
        }
      }
      
      let lat: number, lon: number, city: string;
      
      // Se uma localização específica for fornecida, tenta geocodificar
      if (location) {
        try {
          const response = await fetch(
            `https://api.openweathermap.org/geo/1.0/direct?q=${encodeURIComponent(location)}&limit=1&appid=${WEATHER_API_KEY}`
          );
          const data = await response.json();
          
          if (data && data.length > 0) {
            lat = data[0].lat;
            lon = data[0].lon;
            city = data[0].name;
          } else {
            throw new Error('Localização não encontrada');
          }
        } catch (error) {
          console.error('Erro ao geocodificar localização:', error);
          throw error;
        }
      } else {
        // Caso contrário, usa a localização atual
        const position = await getCurrentPosition();
        lat = position.latitude;
        lon = position.longitude;
        city = await getCityFromCoordinates(lat, lon);
      }
      
      // Obter dados meteorológicos da API
      const response = await fetch(
        `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=${WEATHER_API_KEY}`
      );
      
      const data = await response.json();
      
      if (!data || data.cod !== 200) {
        throw new Error('Erro ao obter dados meteorológicos');
      }
      
      // Calcular chance de chuva com base nas nuvens e umidade
      // (uma aproximação simples - em uma implementação real usaríamos previsões)
      let chanceOfRain = 0;
      if (data.clouds && data.main.humidity) {
        chanceOfRain = Math.min(100, Math.round((data.clouds.all * data.main.humidity) / 100));
      }
      
      return {
        city,
        temperature: Math.round(data.main.temp),
        condition: mapWeatherCondition(data.weather[0].main),
        chanceOfRain,
        high: Math.round(data.main.temp_max),
        low: Math.round(data.main.temp_min),
        icon: `https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png`
      };
    } catch (error) {
      console.error('Erro ao obter dados meteorológicos:', error);
      throw error;
    }
  },

  /**
   * Obtém a localização atual do usuário
   */
  getCurrentLocation: async (): Promise<string> => {
    try {
      // Verificar se a localização está habilitada
      if (Platform.OS === 'android') {
        const isLocationEnabled = await SystemService.isLocationEnabled();
        if (!isLocationEnabled) {
          throw new Error('Localização desabilitada');
        }
      }
      
      const position = await getCurrentPosition();
      return await getCityFromCoordinates(position.latitude, position.longitude);
    } catch (error) {
      console.error('Erro ao obter localização atual:', error);
      throw error;
    }
  }
};

export default WeatherService;
EOF