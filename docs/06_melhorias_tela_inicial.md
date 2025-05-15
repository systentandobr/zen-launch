# Melhorias na Tela Inicial

## Visão Geral

Este documento detalha as melhorias necessárias para completar as funcionalidades da tela inicial do ZenLauncher, incluindo o preenchimento das páginas atualmente vazias (relógios, widgets, fotos, ferramentas) e a implementação de uma experiência mais integrada e minimalista.

## Componentes Principais

### 1. Sistema de Relógios Personalizáveis

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Funcionalidades
- Múltiplos estilos de relógios (analógico, digital, minimalista)
- Personalização de cores, tamanhos e fontes
- Opções de mostrar/ocultar segundos, data, dia da semana
- Suporte a múltiplos fusos horários (opcional)
- Animações suaves para transições entre estilos

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│             Página de Relógios                  │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │                                         │    │
│  │              10:45                      │    │
│  │            Quarta-feira                 │    │
│  │            3 de Maio, 2023              │    │
│  │                                         │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │                                         │    │
│  │             [Relógio Analógico]         │    │
│  │                                         │    │
│  │                                         │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐ │    │
│  │  │Estilo1│  │Estilo2│  │Estilo3│  │Estilo4│ │    │
│  │  └──────┘  └──────┘  └──────┘  └──────┘ │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 2. Gerenciador de Widgets

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 3 dias**

#### Funcionalidades
- Suporte a widgets nativos do Android
- Interface de adição/remoção/reposicionamento
- Grade adaptativa para diferentes tamanhos
- Personalização de aparência dos widgets
- Agrupamento de widgets por tema/função

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│             Página de Widgets                   │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────┐  ┌─────────────────────┐   │
│  │                 │  │                     │   │
│  │  Widget Clima   │  │   Widget Calendário │   │
│  │                 │  │                     │   │
│  │                 │  │                     │   │
│  └─────────────────┘  └─────────────────────┘   │
│                                                 │
│  ┌───────────────────────────────────────────┐  │
│  │                                           │  │
│  │            Widget Notas                   │  │
│  │                                           │  │
│  │                                           │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
│  ┌────────────────┐   ┌────────────────┐        │
│  │                │   │                │        │
│  │  Widget Música │   │ Widget Bateria │        │
│  │                │   │                │        │
│  │                │   │                │        │
│  └────────────────┘   └────────────────┘        │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │            Adicionar Widget             │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3. Visualizador de Fotos e Papel de Parede

**Prioridade: Média** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Funcionalidades
- Galeria de papéis de parede pré-definidos
- Suporte a papéis de parede dinâmicos
- Visualização de fotos recentes da galeria
- Slideshow automático de fotos selecionadas
- Ajuste de cores e efeitos visuais

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│             Página de Fotos                     │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌────────────────────┐ ┌────────────────────┐  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  │ Papel de Parede 1  │ │ Papel de Parede 2  │  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  └────────────────────┘ └────────────────────┘  │
│                                                 │
│  ┌────────────────────┐ ┌────────────────────┐  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  │ Papel de Parede 3  │ │ Papel de Parede 4  │  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  └────────────────────┘ └────────────────────┘  │
│                                                 │
│  Papéis de Parede Dinâmicos                     │
│                                                 │
│  ┌────────────────────┐ ┌────────────────────┐  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  │    Material You    │ │     Paisagens      │  │
│  │                    │ │                    │  │
│  │                    │ │                    │  │
│  └────────────────────┘ └────────────────────┘  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 4. Ferramentas Utilitárias

**Prioridade: Média** | **Complexidade: Média** | **Estimativa: 2 dias**

#### Funcionalidades
- Widgets utilitários integrados (calculadora, notas, etc.)
- Links rápidos para configurações do sistema
- Monitores de sistema (bateria, armazenamento, etc.)
- Controles de mídia unificados
- Atalhos personalizáveis para ações

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│             Página de Ferramentas               │
├─────────────────────────────────────────────────┤
│                                                 │
│  Ferramentas Rápidas                            │
│                                                 │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│  │ Calc │ │ Notas│ │Temper│ │ Conve│ │Lantern│   │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘   │
│                                                 │
│  Monitoramento                                  │
│                                                 │
│  ┌─────────────────┐  ┌─────────────────────┐   │
│  │                 │  │                     │   │
│  │  Bateria: 75%   │  │  Armaz.: 45GB livres│   │
│  │  [████████░░░]  │  │  [██████░░░░░░░░░]  │   │
│  │                 │  │                     │   │
│  └─────────────────┘  └─────────────────────┘   │
│                                                 │
│  Controles de Mídia                             │
│                                                 │
│  ┌───────────────────────────────────────────┐  │
│  │  ♫ Nome da Música - Artista               │  │
│  │  ◄◄  ▐▐  ►► 1:45 / 3:30      🔊 ▁▃▅▇      │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
│  Configurações Rápidas                          │
│                                                 │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│  │ Wi-Fi│ │Bluetoo│ │ Som  │ │Brilho │ │ Dark │   │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘   │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 5. Dock Personalizado

**Prioridade: Alta** | **Complexidade: Baixa** | **Estimativa: 1 dia**

#### Funcionalidades
- Configuração fácil de apps favoritos
- Suporte a pastas de aplicativos
- Possibilidade de ocultar/mostrar rótulos
- Ajuste de tamanho e espaçamento
- Efeitos visuais personalizáveis

#### Esboço de Layout
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│               [Conteúdo da Tela]                │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
├─────────────────────────────────────────────────┤
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐    │
│  │  Tel   │ │  Msg   │ │ Câmera │ │ Chrome │    │
│  └────────┘ └────────┘ └────────┘ └────────┘    │
│                                                 │
│            ┌────────────────┐                   │
│            │  Gaveta de Apps│                   │
│            └────────────────┘                   │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Arquitetura da Tela Inicial

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────┐
│                 HomeFragment                    │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────┐                                │
│  │  ViewPager2 │                                │
│  │             │                                │
│  │  ┌─────────────────────────────────────┐     │
│  │  │         ClockPageFragment           │     │
│  │  └─────────────────────────────────────┘     │
│  │                                              │
│  │  ┌─────────────────────────────────────┐     │
│  │  │        WidgetsPageFragment          │     │
│  │  └─────────────────────────────────────┘     │
│  │                                              │
│  │  ┌─────────────────────────────────────┐     │
│  │  │        PhotosPageFragment           │     │
│  │  └─────────────────────────────────────┘     │
│  │                                              │
│  │  ┌─────────────────────────────────────┐     │
│  │  │        ToolsPageFragment            │     │
│  │  └─────────────────────────────────────┘     │
│  │                                              │
│  └─────────────┘                                │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │                   Dock                  │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │              Quick Settings              │    │
│  └─────────────────────────────────────────┘    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Modelo de Dados

```kotlin
// Configurações do relógio
data class ClockSettings(
    val style: ClockStyle = ClockStyle.DIGITAL,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true,
    val use24HourFormat: Boolean = true,
    val textColor: Int = Color.WHITE,
    val font: String = "default"
)

// Estilo do relógio
enum class ClockStyle {
    DIGITAL,
    ANALOG,
    MINIMAL,
    TEXT,
    BINARY
}

// Informações do widget
data class WidgetInfo(
    val id: Int,
    val componentName: ComponentName,
    val width: Int,
    val height: Int,
    val position: Position
)

// Posição do widget na grade
data class Position(
    val row: Int,
    val column: Int
)

// Configurações do dock
data class DockSettings(
    val apps: List<String> = emptyList(),
    val showLabels: Boolean = true,
    val iconSize: Int = 48,
    val opacity: Float = 0.8f,
    val position: DockPosition = DockPosition.BOTTOM
)

// Posição do dock
enum class DockPosition {
    BOTTOM,
    LEFT,
    RIGHT
}
```

## Implementação dos ViewModels

### 1. ClockPageViewModel

**Prioridade: Alta** | **Complexidade: Média** | **Estimativa: 1 dia**

```kotlin
class ClockPageViewModel(
    application: Application,
    private val getClockSettingsUseCase: GetClockSettingsUseCase,
    private val saveClockSettingsUseCase: SaveClockSettingsUseCase
) : AndroidViewModel(application) {
    
    private val _clockSettings = MutableLiveData<ClockSettings>()
    val clockSettings: LiveData<ClockSettings> = _clockSettings
    
    private val _availableStyles = MutableLiveData<List<ClockStyle>>()
    val availableStyles: LiveData<List<ClockStyle>> = _availableStyles
    
    init {
        loadClockSettings()
        loadAvailableStyles()
    }
    
    fun loadClockSettings() {
        viewModelScope.launch {
            val settings = getClockSettingsUseCase()
            _clockSettings.value = settings
        }
    }
    
    private fun loadAvailableStyles() {
        _availableStyles.value = ClockStyle.values().toList()
    }
    
    fun updateClockStyle(style: ClockStyle) {
        val currentSettings = _clockSettings.value ?: ClockSettings()
        val updatedSettings = currentSettings.copy(style = style)
        
        _clockSettings.value = updatedSettings
        
        viewModelScope.launch {
            saveClockSettingsUseCase(updatedSettings)
        }
    }
    
    // Outros métodos para atualização de configurações do relógio
}
```

### 2. WidgetsPageViewModel

**Prioridade: Alta** | **Complexidade: Alta** | **Estimativa: 2 dias**

```kotlin
class WidgetsPageViewModel(
    application: Application,
    private val getWidgetsUseCase: GetWidgetsUseCase,
    private val addWidgetUseCase: AddWidgetUseCase,
    private val removeWidgetUseCase: RemoveWidgetUseCase,
    private val updateWidgetPositionUseCase: UpdateWidgetPositionUseCase
) : AndroidViewModel(application) {
    
    private val _widgets = MutableLiveData<List<WidgetInfo>>()
    val widgets: LiveData<List<WidgetInfo>> = _widgets
    
    private val _availableWidgets = MutableLiveData<List<AppWidgetProviderInfo>>()
    val availableWidgets: LiveData<List<AppWidgetProviderInfo>> = _availableWidgets
    
    init {
        loadWidgets()
        loadAvailableWidgets()
    }
    
    private fun loadWidgets() {
        viewModelScope.launch {
            val widgetList = getWidgetsUseCase()
            _widgets.value = widgetList
        }
    }
    
    private fun loadAvailableWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(getApplication())
        val providers = appWidgetManager.installedProviders
        
        _availableWidgets.value = providers
    }
    
    fun addWidget(provider: AppWidgetProviderInfo, position: Position) {
        viewModelScope.launch {
            val success = addWidgetUseCase(provider, position)
            if (success) {
                loadWidgets()
            }
        }
    }
    
    fun removeWidget(widgetId: Int) {
        viewModelScope.launch {
            val success = removeWidgetUseCase(widgetId)
            if (success) {
                loadWidgets()
            }
        }
    }
    
    fun updateWidgetPosition(widgetId: Int, newPosition: Position) {
        viewModelScope.launch {
            val success = updateWidgetPositionUseCase(widgetId, newPosition)
            if (success) {
                loadWidgets()
            }
        }
    }
    
    // Outros métodos para gerenciamento de widgets
}
```

## Fluxos de Interação

### 1. Fluxo de Adição de Widget

```
┌───────────────┐     ┌────────────────┐     ┌───────────────┐
│ Pressionar    │────►│  Exibir Lista  │────►│ Selecionar    │
│ "+" na tela   │     │   de Widgets   │     │    Widget     │
└───────────────┘     └────────────────┘     └───────┬───────┘
                                                     │
                                                     ▼
┌───────────────┐     ┌────────────────┐     ┌───────────────┐
│   Widget      │◄────│ Posicionar na  │◄────│ Configurar    │
│  Adicionado   │     │      Tela      │     │ Tamanho/Opções│
└───────────────┘     └────────────────┘     └───────────────┘
```

### 2. Fluxo de Troca de Estilo de Relógio

```
┌───────────────┐     ┌────────────────┐     ┌───────────────┐
│  Pressionar   │────►│  Exibir Lista  │────►│  Selecionar   │
│    Relógio    │     │  de Estilos    │     │    Estilo     │
└───────────────┘     └────────────────┘     └───────┬───────┘
                                                     │
                                                     ▼
┌───────────────┐     ┌────────────────┐     ┌───────────────┐
│   Estilo      │◄────│   Aplicar      │◄────│  Configurar   │
│   Aplicado    │     │   Alterações   │     │   Detalhes    │
└───────────────┘     └────────────────┘     └───────────────┘
```

## Dicas de Implementação

1. **Modularidade**: Cada página deve ser um Fragment independente com seu próprio ViewModel
2. **Persistência**: Utilize DataStore para salvar configurações do usuário
3. **Flexibilidade**: Permitir personalização através de interfaces intuitivas
4. **Transições**: Implementar transições suaves entre as páginas do ViewPager
5. **Responsividade**: Adaptar layouts para diferentes tamanhos e orientações de tela
6. **Otimização**: Carregar widgets e relógios de forma assíncrona para não travar a UI
7. **Acessibilidade**: Garantir que todas as funcionalidades sejam acessíveis

## Próximos Passos

1. Implementar os Fragments para cada página
2. Desenvolver os ViewModels correspondentes
3. Criar repositórios e casos de uso necessários
4. Implementar interfaces de usuário atraentes e minimalistas
5. Integrar com as demais funcionalidades do launcher
6. Testar em diferentes dispositivos e configurações
