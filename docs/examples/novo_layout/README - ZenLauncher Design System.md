# README - MindfulLauncher Design System

## Visão Geral

Este repositório contém o sistema de design completo para o MindfulLauncher, um aplicativo Android focado em bem-estar digital e redução consciente do tempo de tela. O projeto inclui recursos XML estruturados, layouts de exemplo e documentação abrangente para implementação.

## Estrutura do Projeto

```
zen_launcher_design/
├── res/                          # Recursos XML principais
│   ├── values/
│   │   ├── colors.xml           # Paleta de cores completa
│   │   ├── dimens.xml           # Dimensões e espaçamentos
│   │   ├── styles.xml           # Estilos de componentes
│   │   ├── themes.xml           # Temas do aplicativo
│   │   └── attrs.xml            # Atributos customizados
│   ├── drawable/                # Recursos visuais
│   └── anim/                    # Animações
├── sample_project/              # Projeto Android de exemplo
│   └── app/src/main/
│       ├── java/                # Código Java de exemplo
│       ├── res/                 # Recursos do projeto sample
│       └── AndroidManifest.xml
├── design_concept.md            # Conceito inicial de design
├── design_guide.md             # Guia completo de implementação
└── README.md                    # Este arquivo
```

## Características Principais

### Sistema de Cores
- **Paleta Escura:** Base em cinza escuro (#1A1A1A) para reduzir fadiga ocular
- **Verde de Destaque:** (#4CAF50) para ações positivas e progresso
- **Cores Temáticas:** Paleta específica para diferentes categorias de hábitos
- **Acessibilidade:** Contraste otimizado seguindo diretrizes WCAG 2.1

### Tipografia
- **Fonte Principal:** Roboto (Light, Regular, Medium)
- **Escala Modular:** 6 níveis hierárquicos (Display, Headline, Title, Subtitle, Body, Caption)
- **Legibilidade:** Otimizada para diferentes tamanhos de tela e condições de uso

### Componentes
- **Botões:** 4 variações (Primary, Secondary, Outline, Text)
- **Cards:** Design minimalista com elevação sutil
- **Progress Bars:** Indicadores visuais para metas e conquistas
- **Navigation:** Barra inferior com ícones claros

## Como Usar

### 1. Integração Básica

Copie os arquivos da pasta `res/` para o diretório de recursos do seu projeto Android:


### 2. Aplicação de Temas

No seu `AndroidManifest.xml`, aplique o tema principal:

```xml
<application
    android:theme="@style/MindfulLauncherTheme">
```

### 3. Uso de Componentes

Utilize os estilos predefinidos em seus layouts:

```xml
<!-- Botão primário -->
<Button
    style="@style/ZenButton.Primary"
    android:text="Iniciar Foco" />

<!-- Card padrão -->
<androidx.cardview.widget.CardView
    style="@style/ZenCard">
    <!-- Conteúdo do card -->
</androidx.cardview.widget.CardView>
```

### 4. Cores e Dimensões

Referencie cores e dimensões através do sistema de recursos:

```xml
<TextView
    android:textColor="@color/textColorPrimary"
    android:textSize="@dimen/text_size_large"
    android:padding="@dimen/padding_default" />
```

## Projeto Sample

O diretório `sample_project/` contém um aplicativo Android funcional demonstrando a implementação do sistema de design. Inclui:

- **MainActivity:** Tela principal com tempo de uso e Deep Focus
- **DeepFocusActivity:** Interface do modo de foco profundo
- **RankingActivity:** Tela de ranking e gamificação
- **StandbyActivity:** Modo standby para carregamento

### Executando o Sample

1. Abra o projeto `sample_project/` no Android Studio
2. Sincronize as dependências Gradle
3. Execute em dispositivo ou emulador Android

## Diretrizes de Design

### Princípios Fundamentais
1. **Minimalismo Funcional:** Cada elemento tem propósito claro
2. **Calma Visual:** Interface que promove tranquilidade
3. **Feedback Positivo:** Gamificação motivacional sem pressão
4. **Acessibilidade:** Utilizável por todos os usuários

### Melhores Práticas
- Use sempre múltiplos de 8dp para espaçamentos
- Mantenha hierarquia tipográfica consistente
- Reserve cor de destaque para ações importantes
- Teste em diferentes tamanhos de tela e configurações de acessibilidade

## Customização

### Adaptando Cores
Para criar variações de tema, modifique o arquivo `colors.xml`:

```xml
<!-- Tema alternativo -->
<color name="colorAccent">#2196F3</color> <!-- Azul ao invés de verde -->
```

### Adicionando Componentes
Crie novos estilos baseados nos existentes:

```xml
<style name="ZenButton.Special" parent="ZenButton.Primary">
    <item name="backgroundTint">@color/specialColor</item>
</style>
```

## Documentação Completa

Para informações detalhadas sobre implementação, consulte:
- `design_guide.md` - Guia completo com especificações técnicas
- `design_concept.md` - Conceito inicial e filosofia de design

## Compatibilidade

- **Android:** API 21+ (Android 5.0)
- **Material Design:** Versão 3 com adaptações customizadas
- **Acessibilidade:** Compatível com TalkBack e outras tecnologias assistivas

## Contribuição

Para contribuir com melhorias no sistema de design:

1. Mantenha consistência com princípios estabelecidos
2. Teste mudanças em diferentes dispositivos
3. Documente novas adições ou modificações
4. Considere impacto na acessibilidade

## Suporte

Para questões técnicas ou sugestões:
- Consulte a documentação completa em `design_guide.md`
- Analise os exemplos no projeto sample
- Verifique implementação nos arquivos XML de recursos

---

**Desenvolvido por:** Manus AI  
**Versão:** 1.0  
**Licença:** Projeto de demonstração para fins educacionais

