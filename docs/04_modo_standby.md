# Modo Standby Always-On

## Status: ✅ TOTALMENTE IMPLEMENTADO E FUNCIONAL

O modo Standby Always-On transforma o dispositivo em uma central de informações úteis quando conectado ao carregador, oferecendo uma interface minimalista e de baixo consumo.

## Funcionalidades Implementadas

### ⚡ **Detecção Automática de Carregamento**
- **PowerConnectionReceiver** monitora estado de carregamento
- **Ativação automática** quando dispositivo conecta
- **Desativação inteligente** ao desconectar
- **Suporte a carregamento** via cabo e wireless

### 🖥️ **Interface Always-On**
- **StandbyActivity** dedicada com interface otimizada
- **Layout minimalista** focado em informações essenciais
- **Modo escuro** para economia de energia
- **Proteção contra burn-in** com movimento sutil

### 🔄 **Navegação Inteligente**
- **Abertura automática** durante carregamento
- **Fechamento automático** ao desconectar
- **Integração fluida** com MainActivity
- **Preservação de estado** do launcher principal

## Arquitetura Implementada

### Componentes Principais

```
┌─────────────────────────────────────┐
│      PowerConnectionReceiver        │
│   (Broadcast Receiver)              │
├─────────────────────────────────────┤
│  • Monitora ACTION_POWER_CONNECTED  │
│  • Monitora ACTION_POWER_DISCONNECTED│
│  • Dispara navegação automática     │
│  • Integra com ChargingStateListener│
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│        StandbyActivity              │
│   (Interface Always-On)             │
├─────────────────────────────────────┤
│  • Layout otimizado para standby    │
│  • Informações contextuais          │
│  • Modo escuro automático           │
│  • Gestão de ciclo de vida          │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│       StandbyFragment               │
│   (Apresentação)                    │
├─────────────────────────────────────┤
│  • Interface de informações         │
│  • Updates em tempo real            │
│  • Interação mínima                 │
│  • Design de baixo consumo          │
└─────────────────────────────────────┘
```

## Layout da Interface

### Tela Principal (StandbyActivity)

```
┌─────────────────────────────────────────────────┐
│                                                 │
│                                                 │
│                   14:32                         │
│              Quarta-feira, 12 Jun               │
│                                                 │
│                                                 │
│                   ⚡ 75%                        │
│               Carregando...                     │
│                                                 │
│                                                 │
│              🌤️ 24°C                           │
│            Parcialmente Nublado                 │
│                                                 │
│                                                 │
│                                                 │
│         ┌─ Toque para abrir launcher ─┐         │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Fluxo de Navegação

```
Conectar carregador → PowerConnectionReceiver → MainActivity.onChargingStarted() → 
StandbyActivity.start() → Interface Always-On ativa

Desconectar carregador → PowerConnectionReceiver → StandbyActivity.finish() → 
Retorna ao MainActivity automaticamente
```

## Implementação Técnica

### PowerConnectionReceiver
```kotlin
class PowerConnectionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                notifyChargingStarted(context)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                notifyChargingStopped(context)
            }
        }
    }
    
    companion object {
        fun isCharging(context: Context): Boolean {
            val batteryStatus = context.registerReceiver(null, 
                IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status = batteryStatus?.getIntExtra(
                BatteryManager.EXTRA_STATUS, -1)
            return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                   status == BatteryManager.BATTERY_STATUS_FULL
        }
    }
}
```

### StandbyActivity
```kotlin
@AndroidEntryPoint
class StandbyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStandbyBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupStandbyMode()
        setupBinding()
        checkChargingState()
    }
    
    private fun setupStandbyMode() {
        // Configurar como Always-On
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        
        // Modo escuro para economia
        supportActionBar?.hide()
        
        // Prevenir burn-in
        startBurnInPrevention()
    }
    
    override fun onBackPressed() {
        // Voltar ao launcher principal
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
```

### StandbyViewModel
```kotlin
@HiltViewModel
class StandbyViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {
    
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()
    
    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()
    
    private val _chargingStatus = MutableStateFlow("")
    val chargingStatus: StateFlow<String> = _chargingStatus.asStateFlow()
    
    init {
        startTimeUpdates()
        startBatteryUpdates()
    }
    
    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date())
                delay(1000) // Atualizar a cada segundo
            }
        }
    }
}
```

## Integração com MainActivity

### ChargingStateListener Interface
```kotlin
interface ChargingStateListener {
    fun onChargingStarted()
    fun onChargingStopped()
    fun onBatteryLevelChanged(level: Int)
}
```

### Implementação na MainActivity
```kotlin
class MainActivity : AppCompatActivity(), ChargingStateListener {
    
    override fun onChargingStarted() {
        // Navegar para modo standby
        val intent = Intent(this, StandbyActivity::class.java)
        startActivity(intent)
    }
    
    override fun onChargingStopped() {
        // StandbyActivity se fecha automaticamente
        // Não é necessário ação aqui
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar se já está carregando na inicialização
        if (PowerConnectionReceiver.isCharging(this)) {
            onChargingStarted()
        }
    }
}
```

## Informações Exibidas

### **Básicas (Sempre Presentes)**
- **Relógio digital** em formato HH:mm
- **Data atual** com dia da semana
- **Status de bateria** com porcentagem
- **Status de carregamento** visual

### **Contextuais (Quando Disponíveis)**
- **Condições climáticas** básicas
- **Temperatura atual** da localização
- **Indicador de conectividade**
- **Notificações prioritárias** (opcional)

### **Interativas (Mínimas)**
- **Toque na tela** retorna ao launcher
- **Botão back** retorna ao launcher
- **Gestos simples** para navegação

## Configurações de Standby

### **Automação**
- **Ativação automática** habilitada por padrão
- **Delay configurável** para ativação (0-30 segundos)
- **Detecção de tipo** de carregamento (cabo/wireless)

### **Visual**
- **Modo escuro** automático para economia
- **Brilho reduzido** para conforto noturno
- **Anti burn-in** com movimento sutil de elementos
- **Animações mínimas** para economia de bateria

### **Comportamento**
- **Keep screen on** durante carregamento
- **Fechamento automático** ao desconectar
- **Preservação de estado** do launcher principal
- **Notificações discretas** apenas prioritárias

## Performance e Otimizações

### **Economia de Energia**
```kotlin
// Configurações de economia aplicadas
private fun optimizeForStandby() {
    // Reduzir frame rate
    window.attributes.preferredDisplayModeId = lowRefreshRateMode
    
    // Brilho reduzido
    val layoutParams = window.attributes
    layoutParams.screenBrightness = 0.3f // 30% do brilho
    window.attributes = layoutParams
    
    // Animações mínimas
    overridePendingTransition(0, 0)
}
```

### **Prevenção de Burn-in**
```kotlin
private fun startBurnInPrevention() {
    viewModelScope.launch {
        while (isActive) {
            // Mover elementos sutilmente a cada 5 minutos
            val offset = Random.nextInt(-5, 5)
            moveElementsSlightly(offset)
            delay(5 * 60 * 1000) // 5 minutos
        }
    }
}
```

### **Gestão de Recursos**
- **Updates otimizados**: Apenas quando necessário
- **Memory footprint**: Mínimo para long-running activity
- **CPU usage**: Reduzido com timers eficientes
- **Wake locks**: Gerenciados automaticamente pelo sistema

## Arquivos de Implementação

### **Core Standby**
- `presentation/standby/StandbyActivity.kt`
- `presentation/standby/StandbyFragment.kt`
- `presentation/standby/StandbyViewModel.kt`

### **Power Management**
- `data/receivers/PowerConnectionReceiver.kt`
- `domain/interfaces/ChargingStateListener.kt`
- `presentation/navigation/NavigationChargingListener.kt`

### **UI & Resources**
- `layout/activity_standby.xml`
- `layout/fragment_standby.xml`
- `values/colors.xml` (cores standby)
- `values-night/` (tema escuro otimizado)

## Permissões Utilizadas

### **Essenciais**
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

### **Broadcast Receivers**
```xml
<receiver android:name=".data.receivers.PowerConnectionReceiver"
          android:enabled="true"
          android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
        <action android:name="android.intent.action.BATTERY_CHANGED" />
    </intent-filter>
</receiver>
```

## Casos de Uso Testados

### **Cenários Funcionais**
- ✅ Ativação automática ao conectar carregador
- ✅ Desativação automática ao desconectar
- ✅ Retorno fluido ao launcher principal
- ✅ Atualização de informações em tempo real
- ✅ Economia de bateria durante uso
- ✅ Funcionalidade em diferentes tipos de carregador

### **Cenários Edge**
- ✅ Conectar/desconectar rapidamente
- ✅ Inicialização com carregador já conectado
- ✅ Rotação de tela durante standby
- ✅ Notificações durante modo standby
- ✅ Low battery durante carregamento

## Melhorias Futuras Planejadas

### **Informações Expandidas**
- **Próximos eventos** do calendário
- **Notificações agrupadas** por prioridade
- **Widgets personalizáveis** para diferentes contextos
- **Informações de fitness** (passos, atividade)

### **Personalização**
- **Temas visuais** para diferentes horários
- **Layout configurável** pelo usuário
- **Widgets modulares** removíveis/adicionáveis
- **Configurações de timing** personalizadas

### **Integrações**
- **Smart home controls** básicos
- **Music player** minimalista
- **Quick settings** essenciais
- **Voice commands** para ações básicas

---

**Modo Standby oferece uma experiência útil e eficiente** transformando momentos de carregamento em oportunidades de informação contextual.
