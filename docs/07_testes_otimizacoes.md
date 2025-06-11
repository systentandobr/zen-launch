# Testes e Otimizações

## Visão Geral

Este documento descreve as estratégias de testes e otimizações necessárias para garantir que o MindfulLauncher refatorado seja estável, eficiente e tenha um bom desempenho em diferentes dispositivos. As recomendações cobrem testes unitários, de integração e de interface, bem como otimizações de memória, bateria e desempenho geral.

## Estratégias de Teste

### 1. Testes Unitários

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 3 dias**

#### Componentes a Testar
- UseCase classes
- Repository implementations
- ViewModel logic
- Model validations
- Utility functions

#### Ferramentas Recomendadas
- JUnit 5
- Mockito
- Kotlin Coroutines Testing
- AndroidX Test

#### Exemplo de Estrutura de Teste

```kotlin
@RunWith(AndroidJUnit4::class)
class AppUsageRepositoryTest {
    
    @Mock
    private lateinit var usageStatsManager: UsageStatsManager
    
    @Mock
    private lateinit var dataStore: DataStore<Preferences>
    
    private lateinit var repository: UsageStatsRepositoryImpl
    
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = UsageStatsRepositoryImpl(context, dataStore)
    }
    
    @Test
    fun getAppUsageInfo_returnsCorrectUsageInfo() = runBlockingTest {
        // Given
        val packageName = "com.example.app"
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val usageStats = UsageStats().apply {
            this.packageName = packageName
            this.totalTimeInForeground = 60000L // 1 minute
            this.lastTimeUsed = System.currentTimeMillis()
        }
        
        whenever(usageStatsManager.queryUsageStats(any(), any(), any()))
            .thenReturn(listOf(usageStats))
            
        // When
        val result = repository.getAppUsageInfo(packageName, 1)
        
        // Then
        assertEquals(packageName, result.packageName)
        assertEquals(60000L, result.usageTimeToday)
        assertEquals(usageStats.lastTimeUsed, result.lastUsed)
    }
}
```

### 2. Testes de Integração

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 2 dias**

#### Cenários de Teste
- Interação entre Repositórios e DataSources
- Fluxos completos de UseCases
- Integração entre ViewModel e Repositórios
- Persistência de dados

#### Ferramentas Recomendadas
- AndroidX Test
- Room InMemory Database
- Espresso
- Hilt Testing

#### Exemplo de Estrutura de Teste

```kotlin
@RunWith(AndroidJUnit4::class)
class DeepFocusModeIntegrationTest {
    
    private lateinit var focusModeRepository: FocusModeRepository
    private lateinit var appRepository: AppRepository
    private lateinit var startFocusSessionUseCase: StartFocusSessionUseCase
    private lateinit var endFocusSessionUseCase: EndFocusSessionUseCase
    private lateinit var checkAppInFocusModeUseCase: CheckAppInFocusModeUseCase
    
    @Before
    fun setup() {
        // Configuração de repositórios com implementações reais
        // mas com datasources em memória
    }
    
    @Test
    fun startFocusSession_thenCheckAppAccess_thenEndSession() = runBlockingTest {
        // Given
        val allowedPackages = listOf("com.example.allowed")
        val blockedPackage = "com.example.blocked"
        val durationMinutes = 60
        
        // When
        // 1. Inicia sessão de foco
        val session = startFocusSessionUseCase(durationMinutes, allowedPackages)
        
        // 2. Verifica acesso a apps
        val allowedAppAccessible = checkAppInFocusModeUseCase(allowedPackages[0])
        val blockedAppAccessible = checkAppInFocusModeUseCase(blockedPackage)
        
        // 3. Encerra sessão
        val endedSession = endFocusSessionUseCase()
        
        // 4. Verifica acesso após encerramento
        val blockedAppAccessibleAfterEnd = checkAppInFocusModeUseCase(blockedPackage)
        
        // Then
        assertTrue(allowedAppAccessible)
        assertFalse(blockedAppAccessible)
        assertEquals(session.id, endedSession?.id)
        assertTrue(blockedAppAccessibleAfterEnd)
    }
}
```

### 3. Testes de Interface

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 3 dias**

#### Cenários de Teste
- Navegação entre telas
- Interações com elementos de UI
- Responsividade em diferentes tamanhos de tela
- Comportamento correto de ViewPager e RecyclerViews
- Gestão de permissões

#### Ferramentas Recomendadas
- Espresso
- UI Automator
- Screenshot Testing
- Compose UI Testing (se aplicável)

#### Exemplo de Estrutura de Teste

```kotlin
@RunWith(AndroidJUnit4::class)
class DeepFocusModeUITest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun startFocusMode_showsFocusActiveUI() {
        // Navigate to focus settings
        onView(withId(R.id.focus_status))
            .perform(click())
        
        // Configure focus settings
        onView(withId(R.id.duration_slider))
            .perform(setProgress(60))
        
        // Select an app
        onView(withId(R.id.allowed_apps_recycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, click()
                )
            )
        
        // Start focus session
        onView(withId(R.id.start_focus_button))
            .perform(click())
        
        // Verify focus session UI is shown
        onView(withId(R.id.focus_status))
            .check(matches(withText(R.string.focus_active)))
        
        onView(withId(R.id.focus_timer))
            .check(matches(isDisplayed()))
    }
}
```

## Otimizações de Desempenho

### 1. Otimização de Memória

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Técnicas Recomendadas
- Carregamento Lazy de recursos
- Gerenciamento de ciclo de vida de objetos pesados
- Uso de WeakReferences para observers
- Caching inteligente de ícones e dados frequentes
- Liberação de recursos não utilizados

#### Diagrama de Gerenciamento de Memória
```
┌─────────────────────────────────────────────────┐
│            Estratégia de Cache                  │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────┐      ┌─────────────────┐   │
│  │  Cache de Ícones│ ◄──► │  LruCache       │   │
│  └────────┬────────┘      └─────────────────┘   │
│           │                                     │
│           ▼                                     │
│  ┌─────────────────┐      ┌─────────────────┐   │
│  │ Cache de Apps   │ ◄──► │ Room Database   │   │
│  └────────┬────────┘      └─────────────────┘   │
│           │                                     │
│           ▼                                     │
│  ┌─────────────────┐      ┌─────────────────┐   │
│  │Cache de Widgets │ ◄──► │ SharedPreferences│   │
│  └─────────────────┘      └─────────────────┘   │
│                                                 │
└─────────────────────────────────────────────────┘
```

#### Exemplo de Implementação de Cache

```kotlin
class IconCache(context: Context) {
    
    private val maxCacheSize = calculateCacheSize(context)
    private val memoryCache = object : LruCache<String, Drawable>(maxCacheSize) {
        override fun sizeOf(key: String, value: Drawable): Int {
            // Para estimar o tamanho do Drawable em KB
            return if (value is BitmapDrawable) {
                val bitmap = value.bitmap
                bitmap.byteCount / 1024
            } else {
                1
            }
        }
    }
    
    fun getIcon(packageName: String, pm: PackageManager): Drawable {
        // Tentar obter do cache
        memoryCache.get(packageName)?.let { return it }
        
        // Se não estiver em cache, carregar e armazenar
        return try {
            val icon = pm.getApplicationIcon(packageName)
            memoryCache.put(packageName, icon)
            icon
        } catch (e: PackageManager.NameNotFoundException) {
            pm.defaultActivityIcon
        }
    }
    
    fun clear() {
        memoryCache.evictAll()
    }
    
    private fun calculateCacheSize(context: Context): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryClass = am.memoryClass
        // Usar 1/8 da memória disponível para o cache de ícones
        return (1024 * 1024 * memoryClass) / 8
    }
}
```

### 2. Otimização de Bateria

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Técnicas Recomendadas
- Uso eficiente de Alarms e JobScheduler
- Limitar uso de serviços em background
- Fazer batching de operações de rede e banco de dados
- Ajustar frequência de atualizações com base no nível de bateria
- Implementar modo de economia de bateria

#### Diagrama de Estratégia de Background
```
┌─────────────────────────────────────────────────┐
│       Estratégia de Background Processing        │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────┐                            │
│  │  Alta Prioridade │                           │
│  │  - FocusMode    │  → Foreground Service      │
│  │  - Notificações │                            │
│  └─────────────────┘                            │
│                                                 │
│  ┌─────────────────┐                            │
│  │  Média Prioridade│                           │
│  │  - UsageStats   │  → WorkManager (Periódico) │
│  │  - Widgets      │                            │
│  └─────────────────┘                            │
│                                                 │
│  ┌─────────────────┐                            │
│  │ Baixa Prioridade │                           │
│  │  - Indexação    │  → WorkManager (Flex)      │
│  │  - Sincronização│                            │
│  └─────────────────┘                            │
│                                                 │
└─────────────────────────────────────────────────┘
```

#### Exemplo de WorkManager para Sincronização Periódica

```kotlin
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        // Obtém o nível de bateria atual
        val batteryStatus = applicationContext.registerReceiver(
            null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()
        
        // Ajusta a quantidade de trabalho com base no nível de bateria
        val isBatteryLow = batteryPct < 20
        
        return try {
            if (isBatteryLow) {
                // Executa apenas sincronização crítica quando bateria está baixa
                syncCriticalData()
            } else {
                // Sincronização completa quando bateria está ok
                syncAllData()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun syncCriticalData() {
        // Implementação de sincronização mínima
    }
    
    private suspend fun syncAllData() {
        // Implementação de sincronização completa
    }
    
    companion object {
        fun enqueuePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "app_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                syncRequest
            )
        }
    }
}
```

### 3. Otimização de Renderização

**Prioridade: Média** | **Complexidade: Alta** | **Estimativa: 2 dias**

#### Técnicas Recomendadas
- Hierarquias de view achatadas
- Uso adequado de ViewHolder pattern
- Minimização de overdraw
- Pré-computação de layouts
- Hardware acceleration
- Animações eficientes

#### Diagrama de Profundidade de View
```
// Hierarquia Ruim (Profunda)
┌─ ConstraintLayout
   ├─ LinearLayout
   │  ├─ FrameLayout
   │  │  ├─ CardView
   │  │  │  ├─ LinearLayout
   │  │  │  │  ├─ ImageView
   │  │  │  │  └─ TextView
   │  │  │  └─ TextView
   │  └─ LinearLayout
   │     └─ Button
   └─ FrameLayout
      └─ RecyclerView

// Hierarquia Otimizada (Achatada)
┌─ ConstraintLayout
   ├─ ImageView
   ├─ TextView
   ├─ TextView
   ├─ Button
   └─ RecyclerView
```

#### Exemplo de Layout Otimizado

```xml
<!-- Layout Original -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">
    
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <ImageView
            android:id="@+id/app_icon"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_gravity="start|center_vertical" />
        
        <TextView
            android:id="@+id/app_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:layout_marginStart="64dp" />
    </FrameLayout>
</LinearLayout>

<!-- Layout Otimizado -->
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <ImageView
        android:id="@+id/app_icon"
        android:layout_width="48dp"
        android:layout_height="48dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
    
    <TextView
        android:id="@+id/app_name"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        app:layout_constraintStart_toEndOf="@id/app_icon"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

## Monitoramento de Desempenho

### 1. Métricas de Desempenho

**Prioridade: Média** | **Complexidade: Média** | **Estimativa: 1 dia**

#### Métricas a Monitorar
- Tempo de inicialização do launcher
- Tempo de carregamento da lista de apps
- Uso de memória em diferentes estados
- Consumo de CPU durante operações chave
- Taxa de quadros (frame rate) durante animações
- Consumo de bateria em background

#### Ferramentas Recomendadas
- Android Profiler
- Firebase Performance Monitoring
- Custom timers em pontos críticos
- LeakCanary para detecção de vazamentos de memória
- StrictMode para detecção de operações em thread principal

#### Exemplo de Implementação de Métrica Personalizada

```kotlin
class PerformanceMonitor {
    
    private val timers = mutableMapOf<String, Long>()
    private val metrics = mutableMapOf<String, LongArray>()
    
    fun startTimer(name: String) {
        timers[name] = System.nanoTime()
    }
    
    fun stopTimer(name: String) {
        val startTime = timers[name] ?: return
        val duration = System.nanoTime() - startTime
        
        // Armazenar as últimas 10 medições
        val values = metrics[name] ?: LongArray(10)
        
        // Shift array e adicionar novo valor
        for (i in 0 until values.size - 1) {
            values[i] = values[i + 1]
        }
        values[values.size - 1] = duration
        
        metrics[name] = values
        timers.remove(name)
        
        // Log da métrica
        Timber.d("Performance [$name]: ${duration / 1_000_000} ms")
    }
    
    fun getAverageTime(name: String): Double {
        val values = metrics[name] ?: return 0.0
        val sum = values.sum()
        return sum / values.count { it > 0 }.toDouble() / 1_000_000 // em ms
    }
    
    companion object {
        const val APP_LIST_LOAD = "app_list_load"
        const val FOCUS_MODE_TRANSITION = "focus_mode_transition"
        const val LAUNCHER_STARTUP = "launcher_startup"
        const val WIDGET_RENDER = "widget_render"
    }
}
```

### 2. Análise de Crashes e ANRs

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 1 dia**

#### Técnicas Recomendadas
- Integração com Firebase Crashlytics
- Estratégia de logging eficiente
- Captura e relatório de exceções não tratadas
- Monitoramento de ANRs (Application Not Responding)
- Sessões de teste de estresse

#### Exemplo de Configuração de Crashlytics

```kotlin
class MindfulLauncherApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        
        // Configurar Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(true)
            setCustomKey("device_model", Build.MODEL)
            setCustomKey("android_version", Build.VERSION.SDK_INT.toString())
        }
        
        // Configurar handler de exceções não tratadas
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception on thread: ${thread.name}")
            FirebaseCrashlytics.getInstance().recordException(throwable)
            
            // Aqui você pode fazer algo antes do app fechar
            // como salvar o estado atual
            
            // Encaminhar para o handler padrão do sistema
            defaultHandler.uncaughtException(thread, throwable)
        }
    }
    
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
}
```

## Próximos Passos

1. Implementar estrutura básica de testes unitários
2. Configurar testes de integração para componentes críticos
3. Implementar testes de UI para fluxos principais
4. Aplicar otimizações de memória (caching, lazy loading)
5. Implementar padrões de eficiência energética
6. Otimizar layouts e hierarquias de view
7. Configurar monitoramento de desempenho
8. Implementar rastreamento de crashes e ANRs

## Métricas de Sucesso

- Cobertura de testes unitários > 80%
- Todos os fluxos críticos cobertos por testes de UI
- Tempo de inicialização do launcher < 1 segundo
- Uso de memória estável (sem vazamentos)
- 60 FPS em todas as animações
- Impacto mínimo no consumo de bateria (< 5% ao dia)
- Zero ANRs em condições normais de operação
