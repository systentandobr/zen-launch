# Visão Geral - MindfulLauncher

## Status: ✅ PROJETO MADURO E FUNCIONAL

O MindfulLauncher é um launcher Android focado em **produtividade e bem-estar digital**, com recursos avançados de controle de distrações e monitoramento de uso consciente.

## Estado Atual da Aplicação

### ✅ **Funcionalidades Implementadas**
- **Arquitetura Clean** com MVVM + Hilt DI
- **Deep Focus Mode** com timer real e bloqueio de apps
- **Sistema de Bloqueio** com telas personalizadas
- **Modo Standby Always-On** durante carregamento
- **Estatísticas de Uso** detalhadas e ranking
- **Navegação Bottom Tab** com 5 telas principais
- **Monitoramento em Tempo Real** de aplicativos

### 🎯 **Objetivos Atingidos**
- ✅ Launcher funcional substituindo o padrão
- ✅ Controle efetivo de distrações
- ✅ Interface minimalista e responsiva
- ✅ Persistência de dados e configurações
- ✅ Performance otimizada e baixo consumo

## Estrutura Principal

### Navegação (Bottom Navigation)
```
┌─────────────────────────────────────────────────┐
│                MainActivity                     │
├─────────────────────────────────────────────────┤
│                                                 │
│  🏠 Home     📱 Apps     🎯 Focus     🏆 Ranking     📊 Stats  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Telas Principais
1. **🏠 HomeFragment** - Apps favoritos e acesso rápido
2. **📱 AppsFragment** - Lista completa de aplicativos organizados
3. **🎯 FocusFragment** - Deep Focus Mode com timer
4. **🏆 RankingFragment** - Classificação de apps por uso
5. **📊 StatsFragment** - Estatísticas detalhadas de uso

### Activities Especiais
- **StandbyActivity** - Modo Always-On durante carregamento
- **AppBlockScreenActivity** - Tela de bloqueio de aplicativos
- **UsagePermissionActivity** - Solicitação de permissões

## Arquitetura do Sistema

```
┌─────────────────────────────────────┐
│         PRESENTATION                │
├─────────────────────────────────────┤
│  • Fragments & ViewModels           │
│  • Activities & Adapters            │
│  • Navigation & Dialogs             │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│           DOMAIN                    │
├─────────────────────────────────────┤
│  • Entities & Use Cases             │
│  • Repository Interfaces            │
│  • Business Logic                   │
└─────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────┐
│            DATA                     │
├─────────────────────────────────────┤
│  • Repository Implementations       │
│  • Services & Workers               │
│  • Receivers & Data Sources         │
└─────────────────────────────────────┘
```

## Funcionalidades Core

### 🎯 **Deep Focus Mode**
- Timer configurável de 15min a 2h
- Bloqueio automático de apps selecionados
- Estados reativos (Idle → Running → Completed)
- Persistência de sessões entre fechamentos

### 🔒 **Sistema de Bloqueio**
- Interceptação em tempo real de apps
- Telas de bloqueio personalizadas
- Níveis de bloqueio (LOW, MEDIUM, HIGH)
- Integração com Focus Mode

### ⚡ **Modo Standby**
- Ativação automática durante carregamento
- Interface dedicada Always-On
- Detecção via PowerConnectionReceiver

### 📊 **Monitoramento de Uso**
- Coleta via UsageStatsManager
- Estatísticas detalhadas por app
- Ranking automático por tempo de uso
- Métricas de produtividade

## Divisão da Documentação

A documentação está organizada por contextos funcionais:

1. **[Arquitetura e Modelos](01_arquitetura_e_modelos.md)** - Base técnica e entidades
2. **[Deep Focus Mode](02_deep_focus_mode.md)** - Sistema de foco e timer
3. **[Controle de Uso](03_controle_uso.md)** - Monitoramento e bloqueio
4. **[Modo Standby](04_modo_standby.md)** - Interface Always-On
5. **[Lista de Apps](05_lista_apps_minimalista.md)** - Organização de aplicativos
6. **[Tela Inicial](06_melhorias_tela_inicial.md)** - Home e widgets
7. **[Testes e Otimizações](07_testes_otimizacoes.md)** - Qualidade e performance

## Princípios do Projeto

### 🎨 **Design**
- **Minimalismo**: Interface limpa e sem distrações
- **Funcionalidade**: Cada elemento tem propósito claro
- **Responsividade**: Adaptação fluida a diferentes telas

### 🔧 **Técnico**
- **Clean Architecture**: Separação clara de responsabilidades
- **Reatividade**: UI que responde automaticamente a mudanças
- **Performance**: Otimização de memória e bateria

### 🧠 **Bem-estar**
- **Consciência**: Usuário ciente do próprio uso
- **Controle**: Ferramentas para gerenciar distrações
- **Equilíbrio**: Promover uso saudável da tecnologia

## Estado de Implementação

### ✅ **Totalmente Funcional**
- Deep Focus Mode com timer real
- Sistema de bloqueio efetivo
- Estatísticas precisas de uso
- Navegação fluida entre telas
- Modo standby automático

### 🔄 **Em Evolução**
- Interface visual (pode ser mais zen)
- Gamificação e conquistas
- Notificações inteligentes
- Widgets personalizados

### 📋 **Próximos Passos**
- Polish da interface visual
- Expansão das métricas de bem-estar
- Integração com calendário
- Backup na nuvem das configurações

---

**Última Atualização**: Junho 2025  
**Status**: Launcher funcional em produção
