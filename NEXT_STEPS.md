# Próximos Passos - MindfulLauncher 

## 🎯 FASE 3: Deep Focus Mode (PRÓXIMA IMPLEMENTAÇÃO)

### Baseado na Imagem 3 - Interface do Timer de Foco

**OBJETIVO**: Criar uma interface minimalista e poderosa para sessões de foco profundo, substituindo a atual tela de foco por um design moderno e funcional.

---

## 📱 ANÁLISE DA IMAGEM 3

### **Elementos Visuais Identificados:**

#### 1. **Header Minimalista**
- Título "Deep Focus Mode" centralizado
- Subtítulo "Bloqueie distrações e mantenha o foco"
- Background escuro e limpo

#### 2. **Timer Circular Central**
- ⭕ **Círculo grande** com progress ring verde
- ⏰ **Tempo "25:00"** no centro (fonte grande e clara)
- 📝 **"Duração do foco"** como label abaixo

#### 3. **Controle de Duração**
- 🎚️ **Slider horizontal** para ajustar tempo
- 📊 **"Duração: 25 minutos"** como feedback
- ⚡ Range de 15min a 2h provavelmente

#### 4. **Botão de Ação Principal**
- 🟢 **"▶ Iniciar Foco"** - Botão verde prominente
- 📱 **Full-width** e bem visível
- 🎯 **Call-to-action** principal da tela

#### 5. **Apps Bloqueados**
- 🔒 **"Apps que serão bloqueados"** como seção
- 📱 **4 ícones de apps** mostrados (Calculator, Camera, Music, etc)
- 🎨 **Tom avermelhado/escuro** para indicar bloqueio

---

## 🛠️ IMPLEMENTAÇÃO TÉCNICA

### **Tarefa 3.1: Redesign do Layout**
**Arquivo**: `app/src/main/res/layout/fragment_focus.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/deepFocusBackgroundColor"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/padding_default"
        android:gravity="center">

        <!-- Header -->
        <TextView
            style="@style/ZenTextAppearance.Headline"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Deep Focus Mode"
            android:textColor="@color/deepFocusColor" />

        <TextView
            style="@style/ZenTextAppearance.Subtitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Bloqueie distrações e mantenha o foco"
            android:textColor="@color/textColorSecondary" />

        <!-- Timer Circular -->
        <FrameLayout
            android:layout_width="@dimen/focus_timer_size"
            android:layout_height="@dimen/focus_timer_size"
            android:layout_marginTop="@dimen/spacing_xxlarge"
            android:layout_marginBottom="@dimen/spacing_large">

            <!-- Círculo de progresso personalizado -->
            <com.mindfulauncher.presentation.focus.CircularTimerView
                android:id="@+id/circularTimer"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />

        </FrameLayout>

        <!-- Controle de Duração -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Duração do foco"
            android:textColor="@color/textColorSecondary" />

        <SeekBar
            android:id="@+id/durationSlider"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_medium"
            android:max="105"
            android:progress="25" />

        <TextView
            android:id="@+id/durationLabel"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Duração: 25 minutos"
            android:textColor="@color/textColorPrimary" />

        <!-- Botão Principal -->
        <Button
            android:id="@+id/startFocusButton"
            style="@style/ZenButton.Primary.Large"
            android:layout_width="match_parent"
            android:layout_height="@dimen/button_height"
            android:layout_marginTop="@dimen/spacing_xxlarge"
            android:text="▶ Iniciar Foco" />

        <!-- Apps Bloqueados -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_large"
            android:text="🔒 Apps que serão bloqueados"
            android:textColor="@color/textColorSecondary" />

        <LinearLayout
            android:id="@+id/blockedAppsContainer"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_medium"
            android:gravity="center"
            android:orientation="horizontal">
            
            <!-- Apps serão adicionados dinamicamente -->
            
        </LinearLayout>

    </LinearLayout>

</ScrollView>
```

### **Tarefa 3.2: Componente de Timer Circular**
**Arquivo**: `CircularTimerView.kt`

```kotlin
class CircularTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var timeText = "25:00"
    private var isActive = false

    // Paint objects para desenhar
    private val backgroundPaint = Paint()
    private val progressPaint = Paint()
    private val textPaint = Paint()

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setTimeText(text: String) {
        this.timeText = text
        invalidate()
    }

    fun setActive(active: Boolean) {
        this.isActive = active
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Implementar desenho do círculo, progresso e texto
    }
}
```

### **Tarefa 3.3: Atualizar FocusFragment**
**Arquivo**: `FocusFragment.kt`

```kotlin
@AndroidEntryPoint
class FocusFragment : Fragment() {

    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FocusViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTimerControls()
        setupDurationSlider()
        setupStartButton()
        observeViewModelStates()
    }

    private fun setupTimerControls() {
        binding.circularTimer.setTimeText("25:00")
        binding.circularTimer.setProgress(0f)
    }

    private fun setupDurationSlider() {
        binding.durationSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val minutes = 15 + progress // 15min a 120min
                binding.durationLabel.text = "Duração: $minutes minutos"
                binding.circularTimer.setTimeText("${minutes}:00")
                viewModel.setDuration(minutes)
            }
            // ... outros métodos
        })
    }

    private fun setupStartButton() {
        binding.startFocusButton.setOnClickListener {
            viewModel.toggleFocus()
        }
    }
}
```

---

## ⚡ BENEFITS DESTA IMPLEMENTAÇÃO

### **1. UX Melhorada**
- ✅ Interface mais clara e focada
- ✅ Timer visual fácil de entender
- ✅ Controles intuitivos

### **2. Funcionalidade Expandida**
- ✅ Duração customizável (não apenas 25min)
- ✅ Visualização dos apps bloqueados
- ✅ Estados claros (Inativo/Ativo/Pausado)

### **3. Design Consistente**
- ✅ Segue o novo design system
- ✅ Cores e espaçamentos padronizados
- ✅ Componentes reutilizáveis

---

## 🎯 RESULTADO ESPERADO

Após implementar a **Fase 3**, o usuário terá:

1. **Tela de foco completamente redesenhada**
2. **Timer circular funcional e bonito**
3. **Controle de duração flexível**
4. **Lista visual de apps bloqueados**
5. **Interface profissional e minimalista**

---

## 🚀 READY TO START?

**Tudo planejado e documentado!** 

Quando quiser começar a **Fase 3**, podemos:
1. Implementar o novo layout XML
2. Criar o componente CircularTimerView
3. Atualizar o FocusFragment
4. Testar a nova interface

**A base está sólida, agora vamos tornar o Deep Focus Mode incrível! 💪**