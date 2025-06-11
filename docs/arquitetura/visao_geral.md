# Arquitetura do MindfulLauncher

Este documento descreve a arquitetura técnica do MindfulLauncher, seguindo os princípios de Clean Architecture e SOLID.

## Visão Geral da Arquitetura

O MindfulLauncher utiliza uma arquitetura em camadas, separando claramente as responsabilidades:

```
+----------------+
|  Presentation  |
+-------+--------+
        |
+-------v--------+
|     Domain     |
+-------+--------+
        |
+-------v--------+
|      Data      |
+----------------+
```

### Camadas da Arquitetura

#### 1. Camada de Apresentação (Presentation)

Responsável pela UI e interação com o usuário.

- **Activities e Fragments**: Componentes de UI
- **ViewModels**: Gerenciam o estado da UI e atuam como ponte para a camada de domínio
- **Adapters**: Convertam dados para exibição em RecyclerViews
- **Mappers**: Convertam entre modelos de domínio e modelos de apresentação

#### 2. Camada de Domínio (Domain)

Contém toda a lógica de negócios e regras da aplicação.

- **Entidades**: Modelos de negócio
- **Casos de Uso (Use Cases)**: Lógica de negócios específica
- **Interfaces de Repositório**: Abstrações para acesso a dados

#### 3. Camada de Dados (Data)

Responsável pelo acesso e manipulação de dados.

- **Implementações de Repositório**: Implementações concretas das interfaces definidas no domínio
- **Fontes de Dados (DataSources)**: API locais e remotas
- **Modelos de Dados**: Representações para armazenamento
- **Serviços**: Operações em segundo plano

## Fluxo de Dados

1. UI solicita dados via ViewModel
2. ViewModel chama Casos de Uso
3. Casos de Uso obtêm ou manipulam dados via interfaces de Repositório
4. Implementações de Repositório coordenam dados de diferentes fontes
5. Dados retornam através das camadas e são exibidos na UI

## Padrões de Design Utilizados

### 1. Repository Pattern

Utilizado para abstrair o acesso a dados.

**Exemplo**: `AppRepository` define a interface para acesso aos aplicativos instalados, e `AppRepositoryImpl` fornece a implementação concreta.

### 2. Observer Pattern

Utilizado para atualizações reativas com LiveData e Flow.

**Exemplo**: ViewModel expõe dados como `StateFlow` para que a UI possa observar mudanças.

### 3. Factory Pattern

Utilizado para criar instâncias complexas.

**Exemplo**: Factories para criação de notificações.

### 4. Strategy Pattern

Utilizado para comportamentos intercambiáveis.

**Exemplo**: Diferentes estratégias de bloqueio de aplicativos (suave, médio, rígido).

## Injeção de Dependência

O MindfulLauncher utiliza Hilt para injeção de dependência, facilitando:

- Testabilidade
- Desacoplamento
- Gerenciamento do ciclo de vida de objetos

## Comunicação entre Componentes

- **Data Binding**: Para ligação entre UI e dados
- **LiveData/Flow**: Para comunicação reativa
- **Callbacks/Listeners**: Para eventos específicos

## Estrutura de Pacotes

```
com.zenlauncher/
├── data/
│   ├── repositories/
│   ├── datasources/
│   │   ├── local/
│   │   └── system/
│   ├── services/
│   └── models/
├── domain/
│   ├── entities/
│   ├── repositories/
│   └── usecases/
├── presentation/
│   ├── home/
│   ├── apps/
│   ├── focus/
│   ├── stats/
│   ├── settings/
│   └── common/
└── di/
    ├── modules/
    └── qualifiers/
```

## Considerações sobre Testes

- **Testes Unitários**: Para casos de uso e ViewModels
- **Testes de Integração**: Para repositórios e fontes de dados
- **Testes de UI**: Para fluxos completos de usuário

## Princípios SOLID Aplicados

1. **Single Responsibility**: Cada classe tem uma única responsabilidade
2. **Open/Closed**: Extensível sem modificação
3. **Liskov Substitution**: Subtipos substituíveis por seus tipos base
4. **Interface Segregation**: Interfaces específicas para necessidades específicas
5. **Dependency Inversion**: Dependências em abstrações, não implementações

## Tratamento de Erros

- **Arquitetura de Resultado**: `Result<T>` para representar sucesso ou falha
- **Exception Handling**: Tratamento consistente através de try/catch
- **Error Propagation**: Propagação controlada através das camadas

## Persistência de Dados

- **Room**: Para armazenamento estruturado de dados
- **SharedPreferences**: Para preferências do usuário
- **ContentProvider**: Para acesso a dados do sistema

## Aspectos de Segurança

- **Permissões**: Solicitação e verificação apropriadas
- **Validation**: Validação de entrada e saída de dados
- **Sanitization**: Limpeza de dados antes do processamento

Este documento serve como guia para a arquitetura do MindfulLauncher e deve ser consultado durante o desenvolvimento para garantir a consistência e qualidade do código.
