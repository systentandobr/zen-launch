# Guia de Migração de Módulos Nativos para Expo

## Módulos Comuns e Equivalentes no Expo

| Módulo React Native | Equivalente no Expo | Observações |
|---------------------|---------------------|-------------|
| `react-native-camera` | `expo-camera` | Acesso à câmera |
| `react-native-maps` | `expo-location` + `react-native-maps` | O Expo inclui suporte ao react-native-maps |
| `react-native-webview` | `react-native-webview` | O Expo inclui suporte |
| `react-native-fs` | `expo-file-system` | Operações com arquivos |
| `react-native-device-info` | `expo-device` | Informações do dispositivo |
| `react-native-permissions` | `expo-permissions` | Gerenciamento de permissões |
| `react-native-push-notification` | `expo-notifications` | Notificações push |
| `react-native-geolocation` | `expo-location` | Acesso à localização |
| `react-native-image-picker` | `expo-image-picker` | Seleção de imagens |
| `react-native-video` | `expo-av` | Reprodução de áudio/vídeo |
| `react-native-sqlite-storage` | `expo-sqlite` | Banco de dados SQLite |
| `react-native-share` | `expo-sharing` | Compartilhamento |
| `react-native-fbsdk` | `expo-facebook` | Integração com Facebook |
| `react-native-google-signin` | `expo-auth-session` | Autenticação com Google |
| `react-native-svg` | `react-native-svg` | O Expo inclui suporte |
| `react-native-biometrics` | `expo-local-authentication` | Autenticação biométrica |
| `react-native-background-timer` | `expo-background-fetch` | Tarefas em segundo plano |
| `react-native-keychain` | `expo-secure-store` | Armazenamento seguro |
| `react-native-gesture-handler` | `react-native-gesture-handler` | O Expo inclui suporte |
| `react-native-reanimated` | `react-native-reanimated` | O Expo inclui suporte |

## Configuração de plugins Expo

Para alguns módulos nativos, o Expo requer a configuração de plugins no arquivo `app.json` ou `app.config.js`.

Exemplo de configuração no `app.json`:

```json
{
  "expo": {
    "plugins": [
      [
        "expo-camera",
        {
          "cameraPermission": "Permita o acesso à câmera para tirar fotos."
        }
      ],
      [
        "expo-location",
        {
          "locationAlwaysAndWhenInUsePermission": "Permita o acesso à sua localização."
        }
      ]
    ]
  }
}
```

## Módulos não suportados

Se um módulo nativo não for suportado pelo Expo, você terá algumas opções:

1. Usar um módulo alternativo que seja suportado
2. Implementar uma solução diferente usando APIs disponíveis
3. Usar o EAS (Expo Application Services) com configuração de desenvolvimento
4. Criar um módulo de Expo personalizado (avançado)
