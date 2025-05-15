# Modo Standby Always-On

## Visão Geral

O modo Standby Always-On é uma funcionalidade que transforma o ZenLauncher em uma tela de informações útil quando o dispositivo está conectado ao carregador. Este modo exibe informações como hora, data, clima, próximos eventos e outras informações relevantes em uma interface minimalista e de baixo consumo de energia.

## Componentes Principais

### 1. Detector de Carregamento

**Prioridade: Alta** | **Complexidade: Baixa** | **Estimativa: 0.5 dia**

#### Funcionalidades
- Detecção automática de conexão/desconexão do carregador
- Ativação automática do modo Standby quando conectado
- Opções de configuração de atraso para ativação
- Suporte a diferentes tipos de carregamento (cabo, wireless)

#### Diagrama de Fluxo
```
┌────────────────┐     ┌────────────────┐     ┌────────────────┐
│  Dispositivo   │────►│   Verificar    │────►│   Preferências │
│   Conectado    │     │  Configurações │     │   do Usuário   │
└────────────────┘     └────────────────┘     └────────────────┘
                                                       │
                                                       ▼
┌────────────────┐     ┌────────────────┐     ┌────────────────┐
│  Modo Standby  │◄────│ Atraso de      │◄────│   Ativação     │
│    Ativado     │     │ Ativação       │     │   Permitida    │
└────────────────┘     └────────────────┘     └────────────────┘
```

### 2. Interface do Modo Standby

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Funcionalidades
- Exibição de relógio digital/analógico
- Informações de data e clima
- Próximos eventos do calendário
- Notificações pendentes (opcional)
- Rotação automática de widgets informativos

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                                                 │
│                   10:45                         │
│             Quarta-feira, 3 de Maio             │
│                                                 │
│                   23°C                          │
│             Parcialmente Nublado                │
│                                                 │
│                                                 │
│                                                 │
│  Próximos Eventos                               │
│  ─────────────────────────────────────────      │
│  11:30 - Reunião com equipe de marketing        │
│  14:00 - Entrevista com candidato               │
│  16:45 - Revisão de projeto                     │
│                                                 │
│                                                 │
│                                                 │
│  Notificações (3)                               │
│  ─────────────────────────────────────────      │
│  Gmail - 2 novos emails                         │
│  WhatsApp - 5 mensagens não lidas               │
│  Calendario - Lembrete: Reunião em 15 minutos   │
│                                                 │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3. Configurações do Modo Standby

**Prioridade: Média** | **Complexidade: Média** | **Estimativa: 1 dia**

#### Funcionalidades
- Ativação/desativação do modo automático
- Personalização de widgets exibidos
- Ajuste de brilho específico para o modo
- Esquema de cores (dia/noite/automático)
- Tempo de atraso para ativação

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│       Configurações do Modo Standby             │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │ ☑ Ativar modo standby automaticamente   │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  Atraso para ativação                           │
│  ┌───────────────────────────────────┐          │
│  │  0        30s        1m       5m+   │        │
│  └───────────────────────────────────┘          │
│                                                 │
│  Widgets a exibir                               │
│  ┌─────────────────────────────────────────┐    │
│  │ ☑ Relógio                               │    │
│  │ ☑ Data                                  │    │
│  │ ☑ Clima                                 │    │
│  │ ☑ Próximos eventos                      │    │
│  │ ☐ Notificações                          │    │
│  │ ☐ Estatísticas de uso                   │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  Brilho da tela                                 │
│  ┌───────────────────────────────────┐          │
│  │  Min                        Max   │          │
│  └───────────────────────────────────┘          │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │ ☑ Adaptar brilho ao ambiente            │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  Esquema de cores                               │
│  ○ Claro   ○ Escuro   ● Automático             │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Arquitetura de Implementação

### Diagrama de Classes

```
┌────────────────┐     ┌────────────────┐
│ BatteryReceiver │────►│ChargeDetector  │
└────────────────┘     └────────┬───────┘
                                │
                                │
┌────────────────┐     ┌────────▼───────┐
│StandbyRepository│◄────│StandbyRepositoryImpl│
└────────┬───────┘     └──────────────┘
         │
         │
┌────────▼───────┐     ┌────────────────┐
│GetStandbySettings│    │UpdateStandbySettings│
└────────┬───────┘     └────────┬───────┘
         │                      │
         │                      │
         │     ┌───────────────┘
         │     │
┌────────▼─────▼──┐
│StandbyViewModel │
└────────┬────────┘
         │
         │
┌────────▼────────┐
│StandbyFragment  │
└─────────────────┘
```

### Modelo de Dados

```kotlin
// Configurações do modo standby
data class StandbySettings(
    val enabled: Boolean = true,
    val activationDelaySeconds: Int = 30,
    val brightness: Int = -1, // -1 para auto
    val adaptBrightness: Boolean = true,
    val colorScheme: ColorScheme = ColorScheme.AUTO,
    val enabledWidgets: Set<StandbyWidgetType> = defaultWidgets()
) {
    companion object {
        fun defaultWidgets() = setOf(
            StandbyWidgetType.CLOCK,
            StandbyWidgetType.DATE,
            StandbyWidgetType.WEATHER,
            StandbyWidgetType.CALENDAR
        )
    }
}

// Tipos de widgets disponíveis
enum class StandbyWidgetType {
    CLOCK,
    DATE,
    WEATHER,
    CALENDAR,
    NOTIFICATIONS,
    USAGE_STATS
}

// Esquemas de cores
enum class ColorScheme {
    LIGHT,
    DARK,
    AUTO
}

// Estado do carregamento
data class ChargingState(
    val isCharging: Boolean,
    val chargePercentage: Int,
    val chargeType: ChargeType
)

// Tipo de carregamento
enum class ChargeType {
    WIRED,
    WIRELESS,
    UNKNOWN
}
```

## Implementação dos Componentes

### 1. Receptor de Estado da Bateria

**Prioridade: Alta** | **Complexidade: Baixa** | **Estimativa: 0.5 dia**

```kotlin
// Registro no AndroidManifest.xml
<receiver
    android:name=".receivers.BatteryStateReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
    </intent-filter>
</receiver>

// Implementação básica
class BatteryStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                // Ativar modo standby após atraso configurado
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                // Desativar modo standby
            }
        }
    }
}
```

### 2. Fragment do Modo Standby

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 1.5 dias**

O StandbyFragment deve ser projetado como um contêiner que pode exibir e organizar diferentes widgets, tornando-o expansível para adicionar novos tipos de informação.

#### Estrutura do Fragment
```
┌─────────────────────────────────────────────────┐
│               StandbyFragment                   │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │            ClockWidget                  │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │            WeatherWidget                │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │            CalendarWidget               │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │        NotificationsWidget              │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3. Sistema de Widgets Modulares

**Prioridade: Média** | **Complexidade: Alta** | **Estimativa: 2 dias**

Para facilitar a adição de novos widgets e permitir personalização, é recomendável implementar um sistema de widgets modulares.

#### Interface de Widget
```kotlin
interface StandbyWidget {
    fun getView(context: Context): View
    fun updateData()
    fun getType(): StandbyWidgetType
    fun getRefreshRate(): Long // em millisegundos
}
```

#### Registro de Widgets
```kotlin
object StandbyWidgetRegistry {
    private val availableWidgets = mutableMapOf<StandbyWidgetType, StandbyWidget>()
    
    fun registerWidget(widget: StandbyWidget) {
        availableWidgets[widget.getType()] = widget
    }
    
    fun getWidget(type: StandbyWidgetType): StandbyWidget? {
        return availableWidgets[type]
    }
    
    fun getActiveWidgets(settings: StandbySettings): List<StandbyWidget> {
        return settings.enabledWidgets
            .mapNotNull { getWidget(it) }
    }
}
```

## Permissões Necessárias

```xml
<!-- Para receber informações de carregamento -->
<uses-permission android:name="android.permission.BATTERY_STATS" 
    tools:ignore="ProtectedPermissions" />

<!-- Para manter a tela ligada -->
<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- Para acesso ao calendário (eventos) -->
<uses-permission android:name="android.permission.READ_CALENDAR" />

<!-- Para acesso à localização (clima) -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Dicas de Implementação

1. **Economia de Energia**: Use o modo de baixo consumo para as animações e atualizações
2. **Burn-in Prevention**: Implemente pequena movimentação periódica dos elementos para evitar burn-in em telas OLED
3. **Transição Suave**: Crie animações sutis para entrada/saída do modo standby
4. **Personalização**: Permita que o usuário reorganize a ordem dos widgets
5. **Sensibilidade ao Ambiente**: Ajuste o brilho e o esquema de cores com base no sensor de luz
6. **Informações Contextuais**: Priorize exibir informações mais relevantes para o momento do dia
7. **Modo Noturno**: Implemente um modo noite especial com cores mais suaves e informações mínimas

## Próximos Passos

1. Implementar o receptor de estado de carregamento
2. Desenvolver a interface principal do modo standby
3. Criar o sistema de widgets modulares
4. Implementar as configurações personalizáveis
5. Testar em diferentes dispositivos e ambientes
