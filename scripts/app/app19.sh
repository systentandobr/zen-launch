#!/bin/bash

# Script 19 criado automaticamente
echo "Executando claude-ai19.sh"
cat << 'EOF' > src/core/infrastructure/services/AppService.ts
import { AppInfo } from '@state/slices/appsSlice';

// Esta classe será implementada nativamente com Kotlin
class AppService {
  // Obtém a lista de apps instalados
  async getInstalledApps(): Promise<AppInfo[]> {
    // Isso será substituído pela implementação real que usa o módulo nativo
    
    // Mock para desenvolvimento
    return [
      { id: '1', name: 'Chrome', packageName: 'com.android.chrome', isRestricted: false },
      { id: '2', name: 'Gmail', packageName: 'com.google.android.gm', isRestricted: false },
      { id: '3', name: 'Maps', packageName: 'com.google.android.maps', isRestricted: false },
      { id: '4', name: 'YouTube', packageName: 'com.google.android.youtube', isRestricted: false },
      { id: '5', name: 'Calendar', packageName: 'com.google.android.calendar', isRestricted: false },
      { id: '6', name: 'Photos', packageName: 'com.google.android.photos', isRestricted: false },
      { id: '7', name: 'Drive', packageName: 'com.google.android.drive', isRestricted: false },
      { id: '8', name: 'Keep', packageName: 'com.google.android.keep', isRestricted: false },
      { id: '9', name: 'Docs', packageName: 'com.google.android.docs', isRestricted: false },
      { id: '10', name: 'WhatsApp', packageName: 'com.whatsapp', isRestricted: false },
      { id: '11', name: 'Instagram', packageName: 'com.instagram.android', isRestricted: true },
      { id: '12', name: 'Twitter', packageName: 'com.twitter.android', isRestricted: true },
    ];
  }

  // Abre um aplicativo pelo packageName
  async launchApp(packageName: string): Promise<boolean> {
    // Será implementado com chamada ao módulo nativo
    console.log(`Lançando app: ${packageName}`);
    return true;
  }

  // Obtém o ícone do aplicativo
  async getAppIcon(packageName: string): Promise<any> {
    // Será implementado com chamada ao módulo nativo
    return null;
  }

  // Verifica se o app está em execução
  async isAppRunning(packageName: string): Promise<boolean> {
    // Será implementado com chamada ao módulo nativo
    return false;
  }
}

export default new AppService();
EOF

cat << 'EOF' > src/core/infrastructure/services/UsageStatsService.ts
import { AppUsage } from '@state/slices/focusSlice';

// Esta classe será implementada nativamente com Kotlin
class UsageStatsService {
  // Obtém estatísticas de uso de apps
  async getUsageStats(timeStart: number, timeEnd: number): Promise<AppUsage[]> {
    // Mock para desenvolvimento
    return [
      { packageName: 'com.android.chrome', appName: 'Chrome', usageTime: 120, lastUsed: Date.now() - 1000 * 60 * 10 },
      { packageName: 'com.instagram.android', appName: 'Instagram', usageTime: 65, lastUsed: Date.now() - 1000 * 60 * 30 },
      { packageName: 'com.whatsapp', appName: 'WhatsApp', usageTime: 45, lastUsed: Date.now() - 1000 * 60 * 20 },
      { packageName: 'com.google.android.gm', appName: 'Gmail', usageTime: 30, lastUsed: Date.now() - 1000 * 60 * 40 },
      { packageName: 'com.google.android.youtube', appName: 'YouTube', usageTime: 25, lastUsed: Date.now() - 1000 * 60 * 60 },
    ];
  }

  // Verifica se o serviço de estatísticas de uso está habilitado
  async isUsageStatsPermissionGranted(): Promise<boolean> {
    // Será implementado com chamada ao módulo nativo
    return true;
  }

  // Redireciona para as configurações para habilitar estatísticas de uso
  async requestUsageStatsPermission(): Promise<void> {
    // Será implementado com chamada ao módulo nativo
    console.log('Solicitando permissão de estatísticas de uso');
  }
}

export default new UsageStatsService();
EOF

cat << 'EOF' > src/core/infrastructure/services/WeatherService.ts
interface WeatherData {
  city: string;
  temperature: number;
  condition: string;
  chanceOfRain: number;
  high: number;
  low: number;
  icon?: string;
}

class WeatherService {
  // Obtém dados meteorológicos para uma localização
  async getWeatherData(location?: string): Promise<WeatherData> {
    // Implementação futura com API real
    // Por enquanto, retorna dados mock
    return {
      city: location || 'São Paulo',
      temperature: 24,
      condition: 'Clear',
      chanceOfRain: 30,
      high: 31,
      low: 7,
    };
  }

  // Obtém a localização atual do usuário
  async getCurrentLocation(): Promise<string> {
    // Será implementado com permissões de localização
    return 'São Paulo';
  }
}

export default new WeatherService();
EOF