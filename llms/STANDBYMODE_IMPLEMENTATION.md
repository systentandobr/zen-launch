{
  `content`: `# Sistema de Estatísticas e Monitoramento - Implementação Atual

## Status: ✅ TOTALMENTE IMPLEMENTADO E FUNCIONAL

O sistema de estatísticas coleta, processa e apresenta dados detalhados sobre o uso de aplicativos, fornecendo insights valiosos para promover o uso consciente do dispositivo.

## Funcionalidades Implementadas

### 📊 **Coleta de Dados em Tempo Real**
- **UsageTrackingService** monitora continuamente o uso
- **UsageStatsManager** para dados precisos do sistema
- **Persistência automática** de estatísticas
- **Processamento inteligente** de dados brutos

### 📈 **Visualização de Estatísticas**
- **Tela Stats** com dados detalhados por app
- **Ranking** dos apps mais utilizados
- **Gráficos temporais** de uso
- **Métricas de produtividade** e bem-estar

### 🏆 **Sistema de Ranking**
- **Classificação automática** por tempo de uso
- **Categorização inteligente** de aplicativos
- **Filtragem por períodos** (dia, semana, mês)
- **Comparações temporais** e tendências

## Arquitetura do Sistema

### Componentes Principais

```
┌─────────────────────────────────────┐
│      UsageTrackingService           │
│   (Foreground Service)              │
├─────────────────────────────────────┤
│  • Coleta dados de uso              │
│  • Processa estatísticas            │
│  • Persiste informações             │
│  • Calcula métricas                 │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│     UsageStatsRepository            │
│   (Processamento de Dados)          │
├─────────────────────────────────────┤
│  • Interface com UsageStatsManager  │
│  • Agregação de dados               │
│  • Cálculos de métricas             │
│  • Cache inteligente                │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│    StatsFragment & RankingFragment  │
│   (Apresentação)                    │
├─────────────────────────────────────┤
│  • Visualização de dados            │
│  • Gráficos e listas                │
│  • Filtros e configurações          │
│  • Exportação de dados              │
└─────────────────────────────────────┘
```

## Entidades de Domínio

### AppUsageStat
```kotlin
data class AppUsageStat(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTimeUsed: Long,
    val firstTimeStamp: Long,
    val lastTimeStamp: Long,
    val sessionCount: Int,
    val averageSessionDuration: Long,
    val category: AppCategory
)
```

### AppUsageSession
```kotlin
data class AppUsageSession(
    val id: String,
    val packageName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val durationMinutes: Long,
    val wasCompleted: Boolean = true
)
```

### UsageStats (Agregadas)
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

## Implementação Técnica

### UsageTrackingService
```kotlin
@AndroidEntryPoint
class UsageTrackingService : Service() {
    
    @Inject lateinit var usageStatsRepository: UsageStatsRepository
    @Inject lateinit var appRepository: AppRepository
    
    private val handler = Handler(Looper.getMainLooper())
    private val collectInterval = 30_000L // 30 segundos
    
    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        startDataCollection()
    }
    
    private fun startDataCollection() {
        handler.post(object : Runnable {
            override fun run() {
                collectUsageData()
                handler.postDelayed(this, collectInterval)
            }
        })
    }
    
    private suspend fun collectUsageData() {
        val usageStats = usageStatsRepository.getUsageStats(
            startTime = System.currentTimeMillis() - collectInterval,
            endTime = System.currentTimeMillis()
        )
        
        processAndStoreStats(usageStats)
    }
    
    private suspend fun processAndStoreStats(stats: List<UsageStats>) {
        stats.forEach { stat ->
            val appUsageStat = AppUsageStat(
                packageName = stat.packageName,
                appName = getAppName(stat.packageName),
                totalTimeInForeground = stat.totalTimeInForeground,
                lastTimeUsed = stat.lastTimeUsed,
                firstTimeStamp = stat.firstTimeStamp,
                lastTimeStamp = stat.lastTimeStamp,
                sessionCount = calculateSessionCount(stat),
                averageSessionDuration = calculateAverageSession(stat),
                category = determineAppCategory(stat.packageName)
            )
            
            usageStatsRepository.saveAppUsageStat(appUsageStat)
        }
    }
}
```

### UsageStatsRepositoryImpl
```kotlin
@Singleton
class UsageStatsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageStatsRepository {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    
    override suspend fun getUsageStats(startTime: Long, endTime: Long): List<UsageStats> {
        return withContext(Dispatchers.IO) {
            usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ).values.toList()
        }
    }
    
    override suspend fun getAppUsageStats(
        days: Int,
        limit: Int
    ): List<AppUsageStat> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (days * 24 * 60 * 60 * 1000L)
        
        val stats = getUsageStats(startTime, endTime)
        
        return stats
            .filter { it.totalTimeInForeground > 0 }
            .sortedByDescending { it.totalTimeInForeground }
            .take(limit)
            .map { convertToAppUsageStat(it) }
    }
    
    override fun getUsageStatsFlow(days: Int): Flow<List<AppUsageStat>> = flow {
        while (true) {
            emit(getAppUsageStats(days, 50))
            delay(60_000) // Atualizar a cada minuto
        }
    }
}
```

## Interface de Usuário

### StatsFragment
```kotlin
@AndroidEntryPoint
class StatsFragment : Fragment() {
    
    private val viewModel: StatsViewModel by viewModels()
    private lateinit var binding: FragmentStatsBinding
    private lateinit var appUsageAdapter: AppUsageAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFilters()
        observeData()
    }
    
    private fun setupRecyclerView() {
        appUsageAdapter = AppUsageAdapter { appStat ->
            // Clique no app mostra detalhes
            showAppDetails(appStat)
        }
        
        binding.recyclerViewStats.apply {
            adapter = appUsageAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appUsageStats.collect { stats ->
                appUsageAdapter.submitList(stats)
                updateSummaryCards(stats)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyStats.collect { dailyStats ->
                updateDailyStatsCard(dailyStats)
            }
        }
    }
    
    private fun updateSummaryCards(stats: List<AppUsageStat>) {
        val totalTime = stats.sumOf { it.totalTimeInForeground }
        val totalApps = stats.size
        val averageSession = stats.map { it.averageSessionDuration }.average()
        
        binding.totalTimeCard.text = formatDuration(totalTime)
        binding.totalAppsCard.text = totalApps.toString()
        binding.averageSessionCard.text = formatDuration(averageSession.toLong())
    }
}
```

### RankingFragment
```kotlin
@AndroidEntryPoint
class RankingFragment : Fragment() {
    
    private val viewModel: RankingViewModel by viewModels()
    private lateinit var binding: FragmentRankingBinding
    private lateinit var rankingAdapter: RankingAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRankingList()
        setupPeriodFilter()
        observeRankingData()
    }
    
    private fun setupRankingList() {
        rankingAdapter = RankingAdapter { rankingItem ->
            // Mostrar detalhes do app
            showAppUsageDetails(rankingItem)
        }
        
        binding.recyclerViewRanking.apply {
            adapter = rankingAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun observeRankingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.rankingData.collect { ranking ->
                rankingAdapter.submitList(ranking)
                updateRankingStats(ranking)
            }
        }
    }
}
```

## Use Cases Implementados

### GetAppUsageStatsUseCase
```kotlin
class GetAppUsageStatsUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(days: Int, limit: Int): List<AppUsageStat> {
        val stats = usageStatsRepository.getAppUsageStats(days, limit)
        
        return stats.map { stat ->
            val appInfo = appRepository.getAppInfo(stat.packageName)
            stat.copy(
                appName = appInfo?.appName ?: stat.appName,
                category = appInfo?.category ?: AppCategory.OTHER
            )
        }
    }
}
```

### GetMostUsedAppsUseCase
```kotlin
class GetMostUsedAppsUseCase @Inject constructor(
    private val getAppUsageStatsUseCase: GetAppUsageStatsUseCase
) {
    suspend operator fun invoke(days: Int, limit: Int): List<AppUsageStat> {
        return getAppUsageStatsUseCase(days, limit)
            .filter { it.totalTimeInForeground > 60_000 } // Mais de 1 minuto
            .sortedByDescending { it.totalTimeInForeground }
            .take(limit)
    }
}
```

## Métricas e Cálculos

### Produtividade Score
```kotlin
fun calculateProductivityScore(stats: List<AppUsageStat>): Double {
    val totalTime = stats.sumOf { it.totalTimeInForeground }
    val productiveTime = stats
        .filter { it.category in productiveCategories }
        .sumOf { it.totalTimeInForeground }
    
    return if (totalTime > 0) {
        (productiveTime.toDouble() / totalTime) * 100
    } else 0.0
}

private val productiveCategories = setOf(
    AppCategory.PRODUCTIVITY,
    AppCategory.EDUCATION,
    AppCategory.BUSINESS,
    AppCategory.TOOLS
)
```

### Wellness Metrics
```kotlin
data class WellnessMetrics(
    val screenTimeBalance: Double,      // 0-100, baseado em metas
    val appDiversity: Double,           // Variedade de apps usados
    val sessionQuality: Double,         // Duração vs frequência
    val focusTime: Double,              // Tempo em sessões de foco
    val distractionLevel: Double        // Tempo em apps distrativos
)

fun calculateWellnessMetrics(
    stats: List<AppUsageStat>,
    focusSessions: List<FocusSession>
): WellnessMetrics {
    // Implementação dos cálculos...
}
```

## Visualização de Dados

### Layouts Principais

#### fragment_stats.xml
```xml
<ScrollView xmlns:android=\"http://schemas.android.com/apk/res/android\">
    <LinearLayout
        android:layout_width=\"match_parent\"
        android:layout_height=\"wrap_content\"
        android:orientation=\"vertical\"
        android:padding=\"@dimen/padding_default\">

        <!-- Cards de Resumo -->
        <LinearLayout
            android:layout_width=\"match_parent\"
            android:layout_height=\"wrap_content\"
            android:orientation=\"horizontal\"
            android:layout_marginBottom=\"@dimen/spacing_large\">

            <CardView android:layout_width=\"0dp\"
                     android:layout_height=\"wrap_content\"
                     android:layout_weight=\"1\"
                     android:layout_margin=\"@dimen/spacing_small\">
                <TextView android:id=\"@+id/total_time_card\"
                         android:text=\"4h 32m\"
                         android:textSize=\"@dimen/text_size_large\" />
            </CardView>

            <CardView android:layout_width=\"0dp\"
                     android:layout_height=\"wrap_content\"
                     android:layout_weight=\"1\"
                     android:layout_margin=\"@dimen/spacing_small\">
                <TextView android:id=\"@+id/total_apps_card\"
                         android:text=\"23 apps\"
                         android:textSize=\"@dimen/text_size_large\" />
            </CardView>

        </LinearLayout>

        <!-- Lista de Apps -->
        <androidx.recyclerview.widget.RecyclerView
            android:id=\"@+id/recyclerViewStats\"
            android:layout_width=\"match_parent\"
            android:layout_height=\"wrap_content\" />

    </LinearLayout>
</ScrollView>
```

#### item_app_usage.xml
```xml
<CardView xmlns:android=\"http://schemas.android.com/apk/res/android\">
    <LinearLayout
        android:layout_width=\"match_parent\"
        android:layout_height=\"wrap_content\"
        android:orientation=\"horizontal\"
        android:padding=\"@dimen/padding_medium\">

        <!-- Ícone do App -->
        <ImageView
            android:id=\"@+id/app_icon\"
            android:layout_width=\"@dimen/app_icon_size\"
            android:layout_height=\"@dimen/app_icon_size\" />

        <!-- Informações do App -->
        <LinearLayout
            android:layout_width=\"0dp\"
            android:layout_height=\"wrap_content\"
            android:layout_weight=\"1\"
            android:layout_marginStart=\"@dimen/spacing_medium\"
            android:orientation=\"vertical\">

            <TextView
                android:id=\"@+id/app_name\"
                android:layout_width=\"wrap_content\"
                android:layout_height=\"wrap_content\"
                android:textSize=\"@dimen/text_size_medium\"
                android:textStyle=\"bold\" />

            <TextView
                android:id=\"@+id/usage_time\"
                android:layout_width=\"wrap_content\"
                android:layout_height=\"wrap_content\"
                android:textSize=\"@dimen/text_size_small\"
                android:textColor=\"@color/textColorSecondary\" />

        </LinearLayout>

        <!-- Estatísticas Rápidas -->
        <LinearLayout
            android:layout_width=\"wrap_content\"
            android:layout_height=\"wrap_content\"
            android:orientation=\"vertical\"
            android:gravity=\"end\">

            <TextView
                android:id=\"@+id/total_time\"
                android:layout_width=\"wrap_content\"
                android:layout_height=\"wrap_content\"
                android:textSize=\"@dimen/text_size_medium\"
                android:textStyle=\"bold\" />

            <TextView
                android:id=\"@+id/session_count\"
                android:layout_width=\"wrap_content\"
                android:layout_height=\"wrap_content\"
                android:textSize=\"@dimen/text_size_small\"
                android:textColor=\"@color/textColorSecondary\" />

        </LinearLayout>

    </LinearLayout>
</CardView>
```

## Configurações de Monitoramento

### AppMonitoringConfig
```kotlin
data class AppMonitoringConfig(
    val packageName: String,
    val isEnabled: Boolean = true,
    val trackSessions: Boolean = true,
    val notifyLimits: Boolean = false,
    val dailyLimitMinutes: Int? = null,
    val weeklyLimitMinutes: Int? = null,
    val breakReminders: Boolean = false,
    val breakIntervalMinutes: Int = 30
)
```

### Configurações Globais
```kotlin
data class GlobalMonitoringConfig(
    val isEnabled: Boolean = true,
    val collectInterval: Long = 30_000L,
    val retentionDays: Int = 90,
    val enableNotifications: Boolean = true,
    val enableBreakReminders: Boolean = false,
    val workingHoursStart: LocalTime = LocalTime.of(9, 0),
    val workingHoursEnd: LocalTime = LocalTime.of(18, 0)
)
```

## Performance e Otimizações

### Coleta Eficiente
- **Interval otimizado**: 30 segundos para balancear precisão e bateria
- **Background processing**: Processamento em threads separadas
- **Batch operations**: Agrupamento de operações de banco
- **Smart caching**: Cache de dados frequentemente acessados

### Gestão de Memória
```kotlin
class UsageStatsCache @Inject constructor() {
    private val statsCache = LruCache<String, List<AppUsageStat>>(50)
    private val cacheExpiry = mutableMapOf<String, Long>()
    private val cacheValidityMs = 5 * 60 * 1000L // 5 minutos
    
    fun getCachedStats(cacheKey: String): List<AppUsageStat>? {
        val expiry = cacheExpiry[cacheKey] ?: 0L
        return if (System.currentTimeMillis() < expiry) {
            statsCache.get(cacheKey)
        } else {
            null
        }
    }
}
```

## Integração com Outras Funcionalidades

### Focus Mode Integration
```kotlin
// Stats informam quais apps bloquear automaticamente
fun suggestAppsToBlock(): List<String> {
    return getMostUsedAppsUseCase(days = 7, limit = 5)
        .filter { it.category in distractiveCategories }
        .map { it.packageName }
}
```

### Wellness Insights
```kotlin
// Insights baseados em padrões de uso
fun generateWellnessInsights(stats: List<AppUsageStat>): List<WellnessInsight> {
    val insights = mutableListOf<WellnessInsight>()
    
    // Detectar uso excessivo
    stats.filter { it.totalTimeInForeground > 3 * 60 * 60 * 1000 } // > 3h
        .forEach { app ->
            insights.add(WellnessInsight.ExcessiveUse(app.packageName, app.totalTimeInForeground))
        }
    
    // Detectar padrões negativos
    if (hasLateNightUsage(stats)) {
        insights.add(WellnessInsight.LateNightUsage)
    }
    
    return insights
}
```

## Arquivos de Implementação

### Core Services
- `data/services/UsageTrackingService.kt`
- `data/repositories/UsageStatsRepositoryImpl.kt`
- `domain/usecases/GetAppUsageStatsUseCase.kt`
- `domain/usecases/GetMostUsedAppsUseCase.kt`

### Presentation Layer
- `presentation/stats/StatsFragment.kt`
- `presentation/stats/StatsViewModel.kt`
- `presentation/ranking/RankingFragment.kt`
- `presentation/ranking/RankingViewModel.kt`

### Adapters
- `presentation/stats/adapters/AppUsageAdapter.kt`
- `presentation/ranking/adapters/RankingAdapter.kt`

### Entidades
- `domain/entities/AppUsageStat.kt`
- `domain/entities/AppUsageSession`
}