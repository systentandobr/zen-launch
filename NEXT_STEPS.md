# Próximos Passos - MindfulLauncher UI Redesign

## 🎯 Fase 1: Migração para Bottom Navigation (1-2 dias)

### Tarefa 1.1: Implementar Bottom Navigation
**Prioridade**: Crítica  
**Estimativa**: 4-6 horas

**Subtarefas**:
- [ ] Atualizar `activity_main.xml` substituindo ViewPager2 por BottomNavigationView
- [ ] Criar menu de navegação inferior (`menu/bottom_navigation.xml`)
- [ ] Atualizar `MainActivity.kt` para usar FragmentTransaction
- [ ] Remover sistema de page indicators
- [ ] Implementar navegação baseada em Fragment replacement

**Arquivos Afetados**:
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/java/com/zenlauncher/presentation/MainActivity.kt`
- `app/src/main/res/menu/bottom_navigation.xml` (novo)

### Tarefa 1.2: Atualizar Design System
**Prioridade**: Alta  
**Estimativa**: 2-3 horas

**Subtarefas**:
- [ ] Expandir `colors.xml` com nova paleta baseada nas imagens
- [ ] Atualizar `styles.xml` com estilos para cards e componentes
- [ ] Criar `dimens.xml` padronizado
- [ ] Implementar tema escuro refinado em `themes.xml`

**Arquivos Afetados**:
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values/dimens.xml`
- `app/src/main/res/values/themes.xml`

## 🎯 Fase 2: Layout Principal com Cards (2-3 dias)

### Tarefa 2.1: Tela Principal Redesenhada
**Prioridade**: Alta  
**Estimativa**: 6-8 horas

**Implementar baseado na Imagem 2 (10:14 screen)**:
- [ ] Header com relógio e data centralizados
- [ ] Card de tempo de uso com progresso visual
- [ ] Círculo de streak (12 dias) com animação
- [ ] Grid de sugestões de atividades (6 cards)
- [ ] Seção de estatísticas na parte inferior
- [ ] Scroll vertical para melhor navegação

**Arquivos Novos**:
- `layout/fragment_home_new.xml`
- `layout/card_usage_time.xml`
- `layout/card_streak_circle.xml`
- `layout/card_activity_suggestion.xml`
- `layout/item_usage_stat.xml`

### Tarefa 2.2: Componentes Interativos
**Prioridade**: Média  
**Estimativa**: 4-5 horas

**Subtarefas**:
- [ ] Implementar ViewModel para nova home
- [ ] Criar animação para círculo de streak
- [ ] Implementar cliques nas sugestões de atividade
- [ ] Binding de dados reais de tempo de uso
- [ ] Estados de loading para cards dinâmicos

## 🎯 Fase 3: Deep Focus Mode (1-2 dias)

### Tarefa 3.1: Redesign do Deep Focus
**Prioridade**: Alta  
**Estimativa**: 5-6 horas

**Implementar baseado na Imagem 3**:
- [ ] Interface minimalista com timer central
- [ ] Slider para seleção de duração (25:00 padrão)
- [ ] Lista visual de apps que serão bloqueados
- [ ] Botão "Iniciar Foco" proeminente
- [ ] Indicador visual de progresso

**Arquivos Afetados**:
- `layout/fragment_focus.xml`
- `FocusFragment.kt`
- `FocusViewModel.kt`

### Tarefa 3.2: Timer e Controles
**Prioridade**: Média  
**Estimativa**: 3-4 horas

**Subtarefas**:
- [ ] Implementar timer circular customizado
- [ ] Controles de play/pause/stop
- [ ] Notificações durante sessão de foco
- [ ] Persistência de sessões ativas

## 🎯 Fase 4: Sistema de Ranking (2-3 dias)

### Tarefa 4.1: Interface de Ranking
**Prioridade**: Média  
**Estimativa**: 6-8 horas

**Implementar baseado na Imagem 4**:
- [ ] Header com streak atual e melhor sequência
- [ ] Pódio visual para top 3 usuários
- [ ] Lista rolável com ranking completo
- [ ] Tabs para diferentes períodos (Semanal/Mensal/Amigos)
- [ ] Cards de recompensas próximas

**Arquivos Novos**:
- `layout/fragment_ranking.xml`
- `layout/card_ranking_podium.xml`
- `layout/item_ranking_entry.xml`
- `layout/card_next_reward.xml`
- `RankingFragment.kt`
- `RankingViewModel.kt`

### Tarefa 4.2: Sistema de Pontuação
**Prioridade**: Baixa  
**Estimativa**: 4-6 horas

**Subtarefas**:
- [ ] Implementar cálculo de pontos
- [ ] Sistema de streaks
- [ ] Definir recompensas e conquistas
- [ ] Backend mock para ranking (futuro: real)

## 🎯 Fase 5: Configurações Redesenhadas (1 dia)

### Tarefa 5.1: Interface de Configurações
**Prioridade**: Baixa  
**Estimativa**: 3-4 horas

**Implementar baseado na Imagem 1**:
- [ ] Reorganizar seções (Personalização, Foco, Social, Sobre)
- [ ] Toggles visuais melhorados
- [ ] Cards agrupados por categoria
- [ ] Navegação hierárquica para sub-configurações

## 📋 Checklist de Implementação

### Semana 1
- [ ] **Dia 1-2**: Migração para Bottom Navigation (Fase 1)
- [ ] **Dia 3-4**: Layout Principal com Cards (Fase 2) 
- [ ] **Dia 5**: Deep Focus Mode básico (Fase 3.1)

### Semana 2
- [ ] **Dia 1**: Finalizar Deep Focus (Fase 3.2)
- [ ] **Dia 2-3**: Interface de Ranking (Fase 4.1)
- [ ] **Dia 4**: Sistema de Pontuação (Fase 4.2)
- [ ] **Dia 5**: Configurações (Fase 5)

## 🛠️ Considerações Técnicas

### Navigation Component
- **Decisão**: Usar Navigation Component + BottomNavigationView
- **Motivo**: Melhor controle de navegação e lifecycle dos fragments
- **Impacto**: Precisa migrar de ViewPager para Fragment transactions

### ViewBinding vs DataBinding
- **Decisão**: Manter ViewBinding, adicionar DataBinding apenas se necessário
- **Motivo**: Simplicidade e performance
- **Impacto**: Binding manual de dados nos cards dinâmicos

### Estado e Persistência
- **Decisão**: Manter StateFlow/LiveData para UI state
- **Motivo**: Reatividade e lifecycle awareness
- **Impacto**: ViewModels precisam expor novos estados para cards

## 🔍 Testes e Validação

### Testes de UI
- [ ] Navegação entre tabs funciona corretamente
- [ ] Cards de tempo de uso mostram dados reais
- [ ] Animações são suaves e responsivas
- [ ] Deep Focus timer funciona corretamente
- [ ] Ranking mostra dados mockados

### Testes de Performance
- [ ] Scroll vertical é fluido
- [ ] Navegação entre tabs não causa lag
- [ ] Animações não impactam bateria
- [ ] Loading states funcionam corretamente

### Testes de Compatibilidade
- [ ] Android 7.0+ (API 24+)
- [ ] Diferentes tamanhos de tela
- [ ] Modo claro/escuro
- [ ] Diferentes densidades de tela

## 🚨 Riscos e Mitigações

### Risco 1: Quebra de Funcionalidades Existentes
- **Mitigação**: Manter ViewPager em branch separada como fallback
- **Teste**: Validar todas funcionalidades após migração

### Risco 2: Performance com Muitos Cards
- **Mitigação**: Implementar lazy loading e view recycling
- **Teste**: Teste de stress com muitos dados

### Risco 3: Complexidade da Animação de Streak
- **Mitigação**: Usar bibliotecas testadas (Lottie) ou implementação simples
- **Teste**: Testar em dispositivos com diferentes capacidades

---

**Objetivo**: Interface mais intuitiva e visualmente atrativa  
**Timeline**: 2 semanas para implementação completa  
**Milestone 1**: Bottom Navigation funcionando (fim da primeira semana)  
**Milestone 2**: Todas as telas redesenhadas (fim da segunda semana)
