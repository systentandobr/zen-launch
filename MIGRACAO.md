Plano de Refatoração
1. Criar um novo projeto limpo
Em vez de tentar consertar o projeto atual, recomendo criar um novo projeto Android do zero com as seguintes características:

Use a versão mais recente estável do Android Studio (Hedgehog ou Iguana)
Selecione Kotlin 1.8.10 (versão muito estável)
Use um template básico do Android (Empty Activity)
Não ative o Hilt/Dagger inicialmente

2. Substituir a injeção de dependência por um Service Locator
Em vez de usar injeção de dependência baseada em anotações, implemente um padrão Service Locator:
kotlinobject ServiceLocator {
    // Context
    private lateinit var appContext: Context
    
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    
    // Database
    private val database by lazy { 
        Room.databaseBuilder(appContext, AppDatabase::class.java, "zen-launcher-db").build() 
    }
    
    // DAOs
    val appDao by lazy { database.appDao() }
    val usageStatsDao by lazy { database.usageStatsDao() }
    val blockRuleDao by lazy { database.blockRuleDao() }
    
    // Mappers
    val appMapper by lazy { AppMapper() }
    val usageStatsMapper by lazy { UsageStatsMapper() }
    val blockRuleMapper by lazy { BlockRuleMapper() }
    
    // Repositories
    val appRepository by lazy { AppRepositoryImpl(appContext, appMapper, appDao) }
    val usageStatsRepository by lazy { UsageStatsRepositoryImpl(appContext, usageStatsMapper, usageStatsDao) }
    val blockRuleRepository by lazy { BlockRuleRepositoryImpl(blockRuleMapper, blockRuleDao) }
    
    // ViewModels (Factory)
    fun provideAppViewModel(): AppViewModel {
        return AppViewModel(appRepository, usageStatsRepository)
    }
}
3. Migrar o código gradualmente

Migrar modelos de dados: Comece pelos modelos/entities
Migrar DAOs e Database: Configure o Room sem usar kapt/ksp inicialmente
Migrar repositórios: Remova as anotações @Inject e use construtor padrão
Migrar UI: Adapte os ViewModels para usar Factory e Service Locator

4. Exemplo de refatoração para uma classe de repositório
Antes (com injeção de dependência):
kotlinclass AppRepositoryImpl @Inject constructor(
    private val appMapper: AppMapper,
    private val appDao: AppDao,
    @ApplicationContext private val context: Context
) : AppRepository {
    // ...
}
Depois (sem injeção de dependência):
kotlinclass AppRepositoryImpl(
    private val context: Context,
    private val appMapper: AppMapper,
    private val appDao: AppDao
) : AppRepository {
    // ...
}
5. Inicialização na Application
kotlinclass ZenLauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar o Service Locator
        ServiceLocator.initialize(this)
    }
}
6. Usar ViewModelFactory em vez de injeção por viewModels()
kotlinclass MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels { 
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceLocator.provideMainViewModel() as T
            }
        }
    }
}
Vantagens desta abordagem:

Simplicidade: Menos magia por baixo dos panos
Estabilidade: Menos chances de conflitos de versão
Controle: Maior visibilidade do fluxo de dependências
Gradual: Você pode migrar aos poucos, funcionalidade por funcionalidade
Manutenibilidade: Código mais direto é mais fácil de manter no futuro

Esta abordagem pode parecer um retrocesso em relação a frameworks modernos de injeção de dependência, mas vai permitir que você saia deste ciclo de problemas de compatibilidade e tenha um projeto funcional. Mais tarde, quando o projeto estiver estável, você pode considerar migrar para uma solução mais sofisticada, se necessário.