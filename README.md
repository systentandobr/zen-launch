# Zen Launcher

Um launcher para Android com design zenista focado em reduzir distrações e aumentar a produtividade.

## Características

- **Design Minimalista**: Interface limpa e intuitiva para reduzir estímulos visuais desnecessários
- **Modo Foco**: Bloqueio temporário de aplicativos distrativos durante períodos de concentração
- **Rastreamento de Uso**: Estatísticas sobre o tempo gasto em cada aplicativo
- **Gerenciamento de Tarefas**: Lista de tarefas simples integrada
- **Personalização**: Organize seus aplicativos em categorias personalizadas
- **Tela de Bloqueio**: Proteja seu dispositivo com uma tela de bloqueio zenista

## Tecnologias

- **React Native**: Framework para desenvolvimento móvel multiplataforma
- **TypeScript**: Tipagem estática para JavaScript
- **React Navigation**: Navegação entre telas
- **Redux Toolkit**: Gerenciamento de estado global
- **TailwindCSS / NativeWind**: Estilização com classes utilitárias
- **Kotlin**: Módulos nativos para funcionalidades avançadas do Android

## Estrutura do Projeto

O projeto segue princípios de Arquitetura Limpa e SOLID, organizando o código em módulos independentes:

```
src/
├── app/                    # Configuração do app
│   ├── store.ts            # Redux store 
│   └── hooks.ts            # Custom hooks
│
├── modules/                # Módulos funcionais
│   ├── launcher/           # Tela inicial e drawer de apps
│   ├── focus/              # Modo foco e estatísticas de uso
│   ├── tasks/              # Gerenciamento de tarefas
│   ├── weather/            # Widget de clima
│   └── notifications/      # Gerenciamento de notificações
│
├── core/                   # Código compartilhado
│   ├── ui/                 # Componentes de UI reutilizáveis
│   ├── hooks/              # Hooks genéricos
│   └── infrastructure/     # Serviços e módulos nativos
│
├── navigation/             # Configuração de navegação
└── state/                  # Gerenciamento de estado (Redux slices)
```

## Módulos Nativos

O projeto inclui os seguintes módulos nativos em Kotlin:

- **AppUsageStatsModule**: Acesso às estatísticas de uso de aplicativos
- **LauncherIntegrationModule**: Integração com o sistema de launcher Android
- **NotificationListenerModule**: Monitoramento de notificações
- **SystemSettingsModule**: Acesso às configurações do sistema

## Instalação

1. Clone o repositório:
```bash
git clone https://github.com/yourusername/zen-launcher.git
cd zen-launcher
```

2. Instale as dependências:
```bash
pnpm install
```

3. Execute o aplicativo (certifique-se de ter um dispositivo Android conectado ou um emulador em execução):
```bash
npm run android
```

## Permissões Necessárias

O aplicativo requer as seguintes permissões:
- `PACKAGE_USAGE_STATS`: Para monitorar o uso de aplicativos
- `QUERY_ALL_PACKAGES`: Para listar todos os aplicativos instalados
- `BIND_NOTIFICATION_LISTENER_SERVICE`: Para acessar notificações
- (Opcional) `ACCESS_COARSE_LOCATION`: Para o widget de clima

## Contribuição

Contribuições são bem-vindas! Por favor, sinta-se à vontade para enviar pull requests ou abrir issues para melhorias ou correções de bugs.

## Licença

Este projeto é licenciado sob a [Licença MIT](LICENSE).
