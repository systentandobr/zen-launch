# Próximos Passos para Implementação

Este documento detalha as tarefas que precisam ser executadas para completar a funcionalidade de monitoramento de apps e resolver os gargalos identificados.

## 🚨 PRIORIDADE CRÍTICA - Resolver Compilação

### 1. Implementar UsageStatsRepositoryImpl
**Estimativa**: 2-3 horas  
**Complexidade**: Alta  
**Arquivo**: `app/src/main/java/com/zenlauncher/data/repositories/UsageStatsRepositoryImpl.kt`

**Tarefas específicas**:
- [ ] Implementar interface `UsageStatsRepository`
- [ ] Integrar com `UsageStatsManager` do Android
- [ ] Adicionar persistência com DataStore
- [ ] Implementar cache em memória para performance
- [ ] Tratar permissões `PACKAGE_USAGE_STATS`
- [ ] Adicionar logging adequado

**Dependências**:
- Precisa de `Context` e `DataStore<Preferences>`
- Requer verificação de permissão PACKAGE_USAGE_STATS
- Integração com `ServiceLocator` ou Hilt

### 2. Implementar AppMonitoringRepositoryImpl  
**Estimativa**: 1-2 horas  
**Complexidade**: Média  
**Arquivo**: `app/src/main/java/com/zenlauncher/data/repositories/AppMonitoringRepositoryImpl.kt`

**Tarefas específicas**:
- [ ] Implementar persistência de `AppMonitoringConfig`
- [ ] Usar DataStore para salvar configurações
- [ ] Implementar métodos de CRUD
- [ ] Adicionar observabilidade via Flow
- [ ] Cache de configurações para performance

### 3. Criar AppUsageMonitorService Real
**Estimativa**: 3-4 horas  
**Complexidade**: Alta  
**Arquivo**: `app/src/main/java/com/zenlauncher/data/services/AppUsageMonitorService.kt`

**Tarefas específicas**:
- [ ] Implementar como `WorkManager` periodic work
- [ ] Monitorar tempo de uso contínuo por app
- [ ] Detectar thresholds de 1h (warning) e 2h (block)
- [ ] Integrar com `AppMonitoringRepository`
- [ ] Respeitar apps excluídos do monitoramento  
- [ ] Implementar lógica de "snooze" após warning

## 🎯 PRIORIDADE ALTA - Funcionalidades Core

### 4. Integrar Status Visual no AppsFragment
**Estimativa**: 2-3 horas  
**Complexidade**: Média  
**Arquivos**: `AppsFragment.kt`, `AppListAdapter.kt`

**Tarefas específicas**:
- [ ] Adicionar indicador visual para apps bloqueados
- [ ] Mostrar ícone/badge para apps com bloqueio ativo
- [ ] Atualizar `item_app.xml` com indicator
- [ ] Implementar colors/styles para estado bloqueado
- [ ] Integrar com `AppBlockRepository` para status real-time

### 5. Dialog de Informações de Bloqueio (Long Press)
**Estimativa**: 1-2 horas  
**Complexidade**: Baixa  
**Arquivo**: `presentation/common/dialogs/BlockedAppInfoDialog.kt`

**Tarefas específicas**:
- [ ] Criar dialog para mostrar tempo restante de bloqueio
- [ ] Exibir motivo do bloqueio (manual vs. tempo de uso)
- [ ] Mostrar quando o bloqueio termina
- [ ] Interface read-only (usuário não pode interagir)
- [ ] Integração com long press no `AppsFragment`

### 6. Otimizar Performance do AppBlockMonitor
**Estimativa**: 1-2 horas  
**Complexidade**: Média  
**Arquivo**: `AppBlockMonitor.kt`

**Tarefas específicas**:
- [ ] Reduzir polling de 500ms para 2-3 segundos
- [ ] Implementar polling inteligente (só quando necessário)
- [ ] Usar `BroadcastReceiver` para mudanças de foreground
- [ ] Cache de estado para evitar verificações desnecessárias
- [ ] Logs de performance e debugging

## 🔧 PRIORIDADE MÉDIA - Integrações e Melhorias

### 7. Sistema de Configuração de Apps Excluídos
**Estimativa**: 2-3 horas  
**Complexidade**: Média  
**Arquivos**: Settings/Preferences screens

**Tarefas específicas**:
- [ ] Tela de configurações para excluir apps do monitoramento
- [ ] Lista de apps instalados com toggle on/off
- [ ] Integração com `AppMonitoringRepository`
- [ ] Configurações de tempo customizado por app
- [ ] Export/import de configurações

### 8. Melhorar Fluxo de Permissões
**Estimativa**: 1-2 horas  
**Complexidade**: Baixa  
**Arquivo**: `presentation/permissions/UsagePermissionActivity.kt`

**Tarefas específicas**:
- [ ] Tela dedicada para solicitar PACKAGE_USAGE_STATS
- [ ] Tutorial explicando necessidade da permissão
- [ ] Verificação automática de permissão no início do app
- [ ] Fallback graceful quando permissão não concedida
- [ ] Deep link para configurações do sistema

### 9. Persistência de Sessões de "Continue Using"
**Estimativa**: 1 hora  
**Complexidade**: Baixa  
**Arquivos**: `UsageWarningActivity.kt`, repositórios

**Tarefas específicas**:
- [ ] Salvar quando usuário escolhe "continuar por X minutos"
- [ ] Evitar múltiplos warnings no período escolhido
- [ ] Reset automático após período configurado
- [ ] Integração com `AppUsageMonitorService`

## 🧹 PRIORIDADE BAIXA - Refinamentos

### 10. Testes e Qualidade de Código
**Estimativa**: 3-4 horas  
**Complexidade**: Média

**Tarefas específicas**:
- [ ] Testes unitários para Use Cases
- [ ] Testes de integração para repositórios  
- [ ] Testes de UI para dialogs críticos
- [ ] Code coverage report
- [ ] Lint fixes e otimizações

### 11. Métricas e Analytics
**Estimativa**: 2-3 horas  
**Complexidade**: Baixa

**Tarefas específicas**:
- [ ] Logging estruturado para debugging
- [ ] Métricas de uso das funcionalidades
- [ ] Performance monitoring
- [ ] Crash reporting integration
- [ ] User behavior analytics (opcional)

### 12. Documentação e Comments
**Estimativa**: 1-2 horas  
**Complexidade**: Baixa

**Tarefas específicas**:
- [ ] KDoc para todas as classes públicas
- [ ] Comments para lógica complexa
- [ ] README atualizado com novas funcionalidades
- [ ] Architecture decision records (ADRs)

## 🔨 Gargalos de Compilação Identificados

### Issues Críticos que Impedem Compilação:

1. **Missing Implementation**: `UsageStatsRepository` não implementado
   - **Solução**: Criar `UsageStatsRepositoryImpl` (Tarefa #1)
   - **Bloqueador**: Use Cases falham na injeção de dependência

2. **Undefined Service**: `AppUsageMonitorService` referenciado mas inexistente  
   - **Solução**: Implementar serviço real (Tarefa #3)
   - **Bloqueador**: Activities de warning/block não funcionam

3. **Missing DI Configuration**: Repositories não configurados no ServiceLocator
   - **Solução**: Atualizar `ServiceLocator` com novas implementações
   - **Bloqueador**: Injection failures em runtime

4. **Permission Handling**: PACKAGE_USAGE_STATS não está sendo solicitada
   - **Solução**: Implementar fluxo de permissão (Tarefa #8)  
   - **Bloqueador**: Funcionalidade não funciona sem permissão

## 📋 Checklist de Implementação

### Fase 1 - Resolver Compilação (1 dia)
- [ ] Implementar `UsageStatsRepositoryImpl`
- [ ] Implementar `AppMonitoringRepositoryImpl` 
- [ ] Atualizar `ServiceLocator` com novas dependências
- [ ] Criar `AppUsageMonitorService` básico
- [ ] Teste de compilação e build successful

### Fase 2 - Funcionalidades Core (1-2 dias)  
- [ ] Integrar status visual no `AppsFragment`
- [ ] Implementar dialog de informações de bloqueio
- [ ] Otimizar performance do `AppBlockMonitor`
- [ ] Implementar fluxo de permissões
- [ ] Teste de funcionalidades básicas

### Fase 3 - Refinamentos (1 dia)
- [ ] Sistema de configuração de apps excluídos
- [ ] Persistência de "continue using"
- [ ] Testes básicos
- [ ] Documentation e cleanup

## ⚠️ Riscos e Considerações

### Riscos Técnicos:
- **Performance**: Monitoramento contínuo pode impactar bateria
- **Permissions**: Users podem não conceder PACKAGE_USAGE_STATS
- **Android Versions**: Comportamento diferente entre versões do Android
- **Memory**: Cache de configurações pode consumir memória

### Mitigações:
- Polling inteligente e otimizado
- Fallback graceful sem permissões
- Testes em múltiplas versões do Android  
- LRU cache com limits de memória

---

**Objetivo**: Ter funcionalidade completa de monitoramento e bloqueio funcionando em 2-3 dias de desenvolvimento focado.
