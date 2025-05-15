# Desenvolvimento do ZenLauncher

Este documento descreve as implementações realizadas e fornece orientações para o desenvolvimento futuro do ZenLauncher.

## Implementações Realizadas

### Sistema de Temas
Foi implementado um sistema completo de temas que permite ao usuário escolher entre temas predefinidos e criar temas personalizados. 

Componentes principais:
- **LauncherTheme**: Modelo de dados que representa um tema com cores primária, de fundo, de texto e de destaque.
- **IThemeRepository**: Interface que define as operações para gerenciamento de temas.
- **ThemeRepositoryImpl**: Implementação concreta do repositório de temas.
- **Casos de Uso**:
  - `GetCurrentThemeUseCase`: Obtém o tema atual.
  - `SetThemeUseCase`: Define o tema atual.
  - `GetAvailableThemesUseCase`: Obtém os temas disponíveis.
  - `ToggleDarkThemeUseCase`: Alterna entre tema claro e escuro.
  - `CreateCustomThemeUseCase`: Cria um tema personalizado.
- **ThemeViewModel**: Gerencia o estado da UI e a lógica de apresentação dos temas.
- **ThemePickerFragment**: Exibe a lista de temas e permite a seleção.
- **ThemeAdapter**: Adaptador para a lista de temas.
- **CreateThemeDialog**: Diálogo para criação de temas personalizados.
- **Tela de Configurações**: Atualizada para incluir a navegação para o seletor de temas.

### Arquitetura e Design Patterns
- Arquitetura Clean Architecture com separação clara entre camadas.
- Padrão MVVM (Model-View-ViewModel) para a camada de apresentação.
- Uso de Repository Pattern para acesso a dados.
- Use Cases para encapsular a lógica de negócios.
- Dependency Injection via Service Locator.
- Observer Pattern através de LiveData para comunicação entre componentes.

## Próximos Passos

### Funcionalidades a Implementar
1. **Implementar Dialog para Tamanho da Grade**:
   - Criar um diálogo para permitir ao usuário escolher o tamanho da grade de aplicativos.

2. **Suporte a Gestos**:
   - Implementar gestos personalizáveis para ações como abrir a gaveta de aplicativos, as configurações rápidas, etc.

3. **Melhorias no App Drawer**:
   - Adicionar funcionalidade de pesquisa.
   - Implementar categorização de aplicativos.
   - Adicionar suporte a pastas.

4. **Personalização Avançada de Widgets**:
   - Permitir redimensionamento e reposicionamento de widgets na tela inicial.
   - Adicionar suporte a widgets personalizados do ZenLauncher.

5. **Backup e Restauração de Configurações**:
   - Implementar funcionalidade para fazer backup das configurações do launcher.
   - Permitir restaurar configurações de um backup.

### Melhorias Técnicas
1. **Migrar para Dagger/Hilt**:
   - Substituir o Service Locator por uma solução de injeção de dependência mais robusta.

2. **Testes Unitários e de Integração**:
   - Adicionar testes para os casos de uso, repositórios e ViewModels.
   - Implementar testes de integração para fluxos completos.

3. **Melhorias de Desempenho**:
   - Otimizar carregamento de aplicativos e widgets.
   - Implementar cache para melhorar o tempo de resposta.

4. **Suporte a Versões Mais Antigas do Android**:
   - Adaptar o código para suportar versões mais antigas do Android (API 21+).

5. **Documentação**:
   - Melhorar a documentação do código com KDoc.
   - Criar diagramas de arquitetura e fluxos de trabalho.

## Convenções de Codificação

Para manter a qualidade e consistência do código, siga estas convenções:

1. **Nomenclatura**:
   - Classes: PascalCase (ex: `AppListViewModel`)
   - Funções e variáveis: camelCase (ex: `getAllApps()`)
   - Constantes: SNAKE_CASE_UPPERCASE (ex: `MAX_GRID_SIZE`)
   - Arquivos XML: snake_case (ex: `item_app.xml`)

2. **Organização de Código**:
   - Siga a estrutura de diretórios definida no README.md.
   - Agrupe funcionalidades relacionadas em pacotes.
   - Um arquivo por classe (exceto para classes pequenas relacionadas).

3. **Documentação**:
   - Use KDoc para documentar classes e métodos públicos.
   - Adicione comentários para código complexo.

4. **Gestão de Estados**:
   - Use LiveData/Flow para estados observáveis.
   - Evite mutabilidade externa (use MutableLiveData privados).

5. **SOLID**:
   - Siga os princípios SOLID em todas as implementações.
   - Mantenha classes com responsabilidade única.
   - Use interfaces para definir contratos e permitir substituições.

## Conclusão

O ZenLauncher está sendo desenvolvido com foco em minimalismo, desempenho e facilidade de uso. A arquitetura limpa e os princípios SOLID aplicados garantem um código organizado e de fácil manutenção, permitindo a adição de novas funcionalidades de forma modular e testável.

Com o sistema de temas implementado, o próximo foco deve ser a melhor configurabilidade do launcher, seguida pela implementação de funcionalidades avançadas como gestos personalizados e suporte a pastas de aplicativos.