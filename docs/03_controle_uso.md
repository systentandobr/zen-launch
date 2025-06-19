# Controle de Uso de Aplicativos

## Status: ✅ TOTALMENTE IMPLEMENTADO E FUNCIONAL

O sistema de controle de uso monitora, analisa e apresenta estatísticas detalhadas sobre o uso de aplicativos, fornecendo insights valiosos para promover o uso consciente da tecnologia.

## Funcionalidades Implementadas

### 📊 **Monitoramento em Tempo Real**
- **UsageTrackingService** coleta dados continuamente
- **UsageStatsManager** para precisão do sistema
- **Persistência automática** de estatísticas
- **Processamento inteligente** de dados brutos

### 📈 **Visualização de Estatísticas**
- **Tela Stats** com dados detalhados por app
- **Ranking** dos apps mais utilizados
- **Métricas temporais** (dia, semana, mês)
- **Categorização automática** de aplicativos

### 🏆 **Sistema de Ranking**
- **Classificação automática** por tempo de uso
- **Filtragem por períodos** configuráveis
- **Comparações temporais** e tendências
- **Insights de produtividade** baseados em categorias

## Arquitetura do Sistema

### Componentes Principais

```
┌─────────────────────────────────────┐
│      UsageTrackingService           │
│   (Foreground Service)              │
├─────────────────────────────────────┤
│  • Coleta dados via UsageStats      │
│  • Processa e agrega informações    │
│  • Persiste dados automaticamente   │
│  • Calcula métricas em tempo real   │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│     UsageStatsRepository            │
│   (Processamento de Dados)          │
├─────────────────────────────────────┤
│  • Interface com sistema Android    │
│  • Agregação e normalização         │
│  • Cache inteligente de dados       │
│  • Cálculos de métricas complexas   │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│    StatsFragment & RankingFragment  │
│   (Apresentação)                    │
├─────────────────────────────────────┤
│  • Visualização reativa de dados    │
│  • Listas e cards informativos      │
│  • Filtros por período              │
│  • Navegação para detalhes          │
└─────────────────────────────────────┘
```

## Layout das Interfaces

### Tela de Estatísticas (StatsFragment)

```
┌─────────────────────────────────────────────────┐
│                 Estatísticas                    │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │ 4h 32m  │ │ 23 apps │ │ 45 min  │           │
│  │  Total  │ │  Usados │ │ Médio   │           │
│  └─────────┘ └─────────┘ └─────────┘           │
│                                                 │
│              Apps Mais Utilizados               │
│                                                 │
│  📱 Instagram        2h 15m    ████████░░       │
│  📺 YouTube          1h 45m    ██████░░░░       │
│  💬 WhatsApp         45m       ███░░░░░░░       │
│  🐦 Twitter          30m       ██░░░░░░░░       │
│  📧 Gmail            25m       █░░░░░░░░░       │
│                                                 │
│              Por Categoria                      │
│                                                 │
│  🎭 Social Media     3h 30m                    │
│  ⚡ Produtividade    1h 15m                    │
│  🎮 Entretenimento   45m                       │
│  📰 Notícias         20m                       │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Tela de Ranking (RankingFragment)

```
┌─────────────────────────────────────────────────┐
│                    Ranking                      │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │   Hoje  │ │ Semana  │ │   Mês   │           │
│  └─────────┘ └─────────┘ └─────────┘           │
│                                                 │
│  🥇  1º  📱 Instagram       2h 15m             │
│                                                 │
│  🥈  2º  📺 YouTube         1h 45m             │
│                                                 │
│  🥉  3º  💬 WhatsApp        45m                │
│                                                 │
│  📍  4º  🐦 Twitter         30m                │
│                                                 │
│  📍  5º  📧 Gmail           25m                │
│                                                 │
│  📍  6º  🎵 Spotify         20m                │
│                                                 │
│  📍  7º  📷 Camera          15m                │
│                                                 │
│  📍  8º  ⚙️ Configurações   10m                │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Entidades de Dados

### **AppUsageStat**
```kotlin
data class AppUsageStat(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,    // Em milissegundos
    val lastTimeUsed: Long,             // Timestamp
    val sessionCount: Int,              // Número de aberturas
    val averageSessionDuration: Long,   // Duração média
    val category: AppCategory           // Categoria do app
)
```

### **DailyUsageStats**
```kotlin
data class DailyUsageStats(
    val date: LocalDate,
    val totalScreenTime: Long,
    val totalUnlocks: Int,
    val mostUsedApp: String,
    val longestSession: Long,
    val appsUsed: Int,
    val productivityScore: Double
)
```

### **UsageSummary**
```kotlin
data class UsageSummary(
    val totalUsageToday: Long,
    val topApps: List<AppUsageStat>,
    val categoryBreakdown: Map<AppCategory, Long>,
    val comparedToYesterday: Double,
    val weeklyAverage: Long
)
```

## Casos de Uso Implementados

### **GetAppUsageStatsUseCase**
- Obtém estatísticas de uso por app
- Filtragem por período (dias, semanas)
- Ordenação por tempo de uso
- Integração com dados do sistema

### **GetMostUsedAppsUseCase**
- Ranking dos apps mais utilizados
- Configuração de limite de resultados
- Filtragem de apps de sistema
- Cache inteligente para performance

### **ManageAppMonitoringUseCase**
- Configuração de monitoramento por app
- Habilitação/desabilitação de coleta
- Configuração de intervalos
- Gestão de permissões

## Métricas e Cálculos

### **Produtividade Score**
```kotlin
fun calculateProductivityScore(stats: List<AppUsageStat>): Double {
    val totalTime = stats.sumOf { it.totalTimeInForeground }
    val productiveTime = stats
        .filter { it.category in productiveCategories }
        .sumOf { it.totalTimeInForeground }
    
    return (productiveTime.toDouble() / totalTime) * 100
}
```

### **Wellness Metrics**
- **Screen Time Balance** - Baseado em metas pessoais
- **App Diversity** - Variedade de apps utilizados
- **Session Quality** - Duração vs frequência
- **Focus Time** - Tempo em apps produtivos
- **Distraction Level** - Tempo em apps distrativos

### **Categorização Inteligente**
```kotlin
enum class AppCategory {
    PRODUCTIVITY,    // Produtividade e trabalho
    SOCIAL,         // Redes sociais
    ENTERTAINMENT,  // Entretenimento e mídia
    GAMES,          // Jogos
    EDUCATION,      // Educação e aprendizado
    BUSINESS,       // Negócios e finanças
    TOOLS,          // Ferramentas do sistema
    HEALTH,         // Saúde e fitness
    NEWS,           // Notícias e informação
    COMMUNICATION,  // Comunicação
    OTHER           // Outros
}
```

## Integração com Outras Funcionalidades

### **Focus Mode Integration**
```kotlin
// Stats sugerem apps para bloquear automaticamente
fun suggestAppsToBlock(): List<String> {
    return getMostUsedAppsUseCase(days = 7, limit = 5)
        .filter { it.category in distractiveCategories }
        .map { it.packageName }
}
```

### **Ranking para Bloqueio**
- Apps mais usados aparecem automaticamente no Focus Mode
- Sugestões inteligentes baseadas em padrões
- Integração com configurações de bloqueio

### **Insights de Bem-estar**
```kotlin
// Geração de insights baseados em padrões
fun generateWellnessInsights(stats: List<AppUsageStat>): List<WellnessInsight> {
    val insights = mutableListOf<WellnessInsight>()
    
    // Detectar uso excessivo (>3h em um app)
    stats.filter { it.totalTimeInForeground > 3 * 60 * 60 * 1000 }
        .forEach { app ->
            insights.add(WellnessInsight.ExcessiveUse(app.packageName))
        }
    
    // Detectar padrões negativos
    if (hasLateNightUsage(stats)) {
        insights.add(WellnessInsight.LateNightUsage)
    }
    
    return insights
}
```

## Performance e Otimizações

### **Coleta Eficiente**
- **Interval otimizado**: 30 segundos para balancear precisão e bateria
- **Background processing**: Threads separadas para processamento
- **Batch operations**: Agrupamento de operações de persistência
- **Smart caching**: Cache de dados frequentemente acessados

### **Gestão de Memória**
```kotlin
class UsageStatsCache {
    private val statsCache = LruCache<String, List<AppUsageStat>>(50)
    private val cacheExpiry = mutableMapOf<String, Long>()
    private val cacheValidityMs = 5 * 60 * 1000L // 5 minutos
    
    // Cache inteligente com expiração automática
}
```

### **Otimizações Aplicadas**
- **Foreground service** evita kill pelo sistema
- **Notification discreta** para transparência
- **Wake locks mínimos** apenas quando necessário
- **Database indexing** para queries rápidas

## Permissões e Configuração

### **Permissão Crítica**
```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
```

### **Fluxo de Permissão**
```
App iniciado → Verifica permissão → Se negada → UsagePermissionActivity → Settings → Retorna ao app
```

### **UsagePermissionActivity**
- Interface amigável explicando a necessidade
- Botão direto para configurações do sistema
- Verificação automática após retorno
- Fallback gracioso se permissão negada

## Arquivos de Implementação

### **Core Services**
- `data/services/UsageTrackingService.kt`
- `data/repositories/UsageStatsRepositoryImpl.kt`
- `domain/usecases/GetAppUsageStatsUseCase.kt`
- `domain/usecases/GetMostUsedAppsUseCase.kt`

### **Presentation Layer**
- `presentation/stats/StatsFragment.kt`
- `presentation/stats/StatsViewModel.kt`
- `presentation/ranking/RankingFragment.kt`
- `presentation/ranking/RankingViewModel.kt`

### **UI Components**
- `presentation/stats/adapters/AppUsageAdapter.kt`
- `presentation/ranking/adapters/RankingAdapter.kt`
- `presentation/ranking/model/RankingModels.kt`

### **Permission Handling**
- `presentation/permissions/UsagePermissionActivity.kt`

## Estatísticas Coletadas

### **Por App**
- Tempo total de uso (foreground)
- Número de sessões/aberturas
- Duração média por sessão
- Última utilização
- Categoria do aplicativo

### **Agregadas**
- Tempo total de tela por dia
- Número de desbloqueios
- App mais usado do dia
- Sessão mais longa
- Score de produtividade

### **Tendências**
- Comparação com dias anteriores
- Médias semanais/mensais
- Padrões de uso por horário
- Evolução do uso ao longo do tempo

## Melhorias Futuras Planejadas

### **Visualizações Avançadas**
- Gráficos temporais de uso
- Heatmaps de atividade por horário
- Comparações com médias gerais
- Projeções baseadas em tendências

### **Insights Inteligentes**
- Detecção de padrões problemáticos
- Sugestões personalizadas de melhoria
- Alertas de uso excessivo
- Celebração de conquistas

### **Gamificação**
- Metas de redução de tempo de tela
- Conquistas por uso consciente
- Streaks de dias com uso equilibrado
- Comparações sociais (opcional)

---

**Sistema robusto que fornece insights valiosos** para promover o uso consciente e saudável da tecnologia móvel.
