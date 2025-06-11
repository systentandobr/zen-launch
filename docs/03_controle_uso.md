# Controle de Uso de Aplicativos

## Visão Geral

O módulo de Controle de Uso permite monitorar e limitar o tempo de uso de aplicativos, ajudando usuários a estabelecerem hábitos digitais mais saudáveis. Esta funcionalidade complementa o Deep Focus Mode, fornecendo análises detalhadas de uso e aplicando limites específicos por aplicativo.

## Componentes Principais

### 1. Estatísticas de Uso

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 3 dias**

#### Funcionalidades
- Rastreamento de tempo de uso por aplicativo
- Visualização de estatísticas diárias, semanais e mensais
- Detecção de padrões de uso (horários de pico, frequência)
- Categorização de aplicativos por tipo de uso

#### Diagrama de Estrutura
```
┌─────────────────────────────────────────────────┐
│            Estatísticas de Uso                  │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │     Gráfico de Uso Diário/Semanal       │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │      Top 5 Aplicativos Mais Usados      │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │       Categorias de Aplicativos         │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │      Estatísticas por Aplicativo        │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 2. Limites de Uso

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 2 dias**

#### Funcionalidades
- Definição de limites diários por aplicativo
- Lembretes e alertas próximos ao limite
- Bloqueio automático após atingir o limite (opcional)
- Exibição de tempo restante durante o uso

#### Diagrama de Estrutura
```
┌─────────────────────────────────────────────────┐
│               Limites de Uso                    │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │    Configuração de Limite por App       │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │  Lista de Apps com Limites Definidos    │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │   Opções de Bloqueio e Notificação      │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3. Notificações e Alertas

**Prioridade: Média** | **Complexidade: Média** | **Estimativa: 1 dia**

#### Funcionalidades
- Alertas quando se aproxima do limite de uso
- Notificações de resumo diário de uso
- Feedback positivo ao manter uso abaixo dos limites
- Opções de personalização de alertas

#### Exemplos de Notificações
```
┌─────────────────────────────────────────────────┐
│ MindfulLauncher                                 now │
├─────────────────────────────────────────────────┤
│ Limite de Uso: Instagram                        │
│ Você tem 5 minutos restantes hoje.              │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│ MindfulLauncher                                 now │
├─────────────────────────────────────────────────┤
│ Resumo de Uso: Hoje                             │
│ Tempo total: 3h 45min                           │
│ Top app: YouTube (1h 20min)                     │
└─────────────────────────────────────────────────┘
```

## Arquitetura de Implementação

### Diagrama de Classes

```
┌───────────────────┐       ┌───────────────────┐
│  UsageStatsManager │◄──────│ UsageStatsService │
└───────────────────┘       └─────────┬─────────┘
                                      │
                                      │
┌───────────────────┐       ┌─────────▼─────────┐
│  UsageRepository  │◄──────│UsageRepositoryImpl│
└────────┬──────────┘       └───────────────────┘
         │
         │
┌────────▼──────────┐       ┌───────────────────┐
│GetAppUsageUseCase │       │SetUsageLimitUseCase│
└────────┬──────────┘       └────────┬──────────┘
         │                           │
         │                           │
         │        ┌──────────────────┘
         │        │
┌────────▼────────▼───────┐
│   UsageViewModel        │
└────────┬────────────────┘
         │
         │
┌────────▼────────────────┐
│   UsageStatsFragment    │
└─────────────────────────┘
```

### Modelo de Dados

```kotlin
// Informações de uso de um aplicativo
data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val usageTimeToday: Long,       // em milissegundos
    val usageTimeWeek: Long,        // em milissegundos
    val launchCount: Int,           // número de vezes que o app foi aberto hoje
    val lastTimeUsed: Long          // timestamp da última utilização
)

// Limites de uso definidos pelo usuário
data class UsageLimit(
    val packageName: String,
    val dailyLimitMinutes: Int,     // limite diário em minutos
    val isBlockingEnabled: Boolean, // se deve bloquear após o limite
    val notifyAtPercentage: Int     // notificar quando atingir esta porcentagem
)

// Resumo de uso para a dashboard
data class UsageSummary(
    val totalUsageToday: Long,      // tempo total de uso hoje
    val topApps: List<AppUsageInfo>, // apps mais usados
    val categorySummary: Map<String, Long> // uso por categoria
)
```

## Interfaces de Usuário

### 1. Tela de Estatísticas de Uso

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│           Estatísticas de Uso                   │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐      │
│  │   Hoje    │ │  Semana   │ │    Mês    │      │
│  └───────────┘ └───────────┘ └───────────┘      │
│                                                 │
│  Tempo total de uso: 3h 45min                   │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │                                         │    │
│  │                                         │    │
│  │     Gráfico de Uso por Categoria        │    │
│  │                                         │    │
│  │                                         │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  Aplicativos Mais Usados                        │
│                                                 │
│  ┌────────┐ Instagram         1h 30min     ┌──┐ │
│  │  Icon  │                               │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
│  ┌────────┐ YouTube           1h 05min     ┌──┐ │
│  │  Icon  │                               │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
│  ┌────────┐ WhatsApp          45min        ┌──┐ │
│  │  Icon  │                               │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 2. Tela de Configuração de Limites

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 1 dia**

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│           Configurar Limites                    │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │  Pesquisar aplicativo                   │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  Aplicativos com Limites                        │
│                                                 │
│  ┌────────┐ Instagram                      ┌──┐ │
│  │  Icon  │ Limite: 1h/dia                │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
│  ┌────────┐ TikTok                         ┌──┐ │
│  │  Icon  │ Limite: 30min/dia             │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
│  Outros Aplicativos                             │
│                                                 │
│  ┌────────┐ YouTube                        ┌──┐ │
│  │  Icon  │ Sem limite                    │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
│  ┌────────┐ Twitter                        ┌──┐ │
│  │  Icon  │ Sem limite                    │>│ │ │
│  └────────┘                               └──┘ │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3. Diálogo de Configuração de Limite

**Prioridade: Média** | **Complexidade: Baixa** | **Estimativa: 0.5 dia**

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│    Configurar Limite para Instagram             │
├─────────────────────────────────────────────────┤
│                                                 │
│  Tempo limite diário                            │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │              1 hora                     │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌───────────────────────────────────┐          │
│  │  0           1h           2h     3h+  │      │
│  └───────────────────────────────────┘          │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │ ☑ Bloquear app após atingir o limite    │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │ ☑ Notificar quando atingir 80% do limite│    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌───────────┐             ┌───────────────┐    │
│  │  Cancelar │             │    Salvar     │    │
│  └───────────┘             └───────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Permissões Necessárias

Para implementar o controle de uso, é necessário solicitar a permissão `PACKAGE_USAGE_STATS`, que requer aprovação manual do usuário nas configurações do sistema.

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" 
    tools:ignore="ProtectedPermissions" />
```

## Fluxo de Solicitação de Permissão

```
┌────────────────┐     ┌────────────────┐     ┌────────────────┐
│ Tela Inicial   │────►│Explicação sobre│────►│  Configurações │
│                │     │   permissão    │     │  do Sistema    │
└────────────────┘     └────────────────┘     └────────────────┘
        ▲                                              │
        │                                              │
        │                                              ▼
┌────────────────┐                          ┌────────────────┐
│ Funcionalidade │◄─────────────────────────│  Permissão     │
│   Ativada      │                          │  Concedida     │
└────────────────┘                          └────────────────┘
```

## Dicas de Implementação

1. **UsageStatsManager**: Use a API do sistema para acessar as estatísticas de uso
2. **Armazenamento**: Utilize DataStore para salvar limites e configurações de uso
3. **Background Service**: Implemente um serviço em segundo plano para monitorar o uso
4. **Categorização**: Use a Google Play Store API ou técnicas de heurística para categorizar apps
5. **Performance**: Cache os dados de uso para evitar cálculos frequentes
6. **Accuracy**: Verifique os dados de uso em períodos regulares para maior precisão
7. **Battery**: Otimize os intervalos de verificação para evitar consumo excessivo de bateria

## Próximos Passos

1. Implementar o repositório de estatísticas de uso
2. Criar os casos de uso relacionados
3. Desenvolver as interfaces de usuário
4. Implementar o sistema de notificações
5. Testar em diferentes dispositivos e versões do Android
