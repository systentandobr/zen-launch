# Changelog

## v1.0.0 (13/05/2025)

### Adicionado
- Implementação inicial do MindfulLauncher com foco em minimalismo e bem-estar digital
- Interface de usuário limpa e sem distrações usando Jetpack Compose
- Lista de aplicativos minimalista com pesquisa e organização alfabética
- Modo Deep Focus para bloqueio de aplicativos distrativos durante períodos de concentração
- Estatísticas de uso detalhadas para monitorar o tempo gasto em aplicativos
- Limites de uso configuráveis para aplicativos com notificações de aviso
- Modo Always On para quando o dispositivo está conectado ao carregador, com relógio e informações de bateria
- Serviço de background para monitoramento de uso e aplicação de limites

### Recursos Técnicos
- Arquitetura Clean com MVVM
- Injeção de dependência com Hilt
- Persistência de dados com DataStore
- Programação reativa com Kotlin Flow
- Componentes de UI com Jetpack Compose
- Permissões de sistema para acessar estatísticas de uso e sobrepor outras aplicações
- Broadcast Receivers para detecção de conexão de carregador
- Serviços em primeiro e segundo plano
