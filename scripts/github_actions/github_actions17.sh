#!/bin/bash

# Script 17 criado automaticamente
echo "Executando claude-ai17.sh"
cat << 'EOF' > CONTRIBUTING.md
# Guia de Contribuição - Zen Launcher

Obrigado pelo interesse em contribuir com o Zen Launcher! Este documento fornece orientações para ajudá-lo no processo de contribuição.

## Primeiros Passos

### Configuração do Ambiente

1. **Requisitos:**
   - Node.js (versão 16 ou superior)
   - JDK 11
   - Android Studio (com Android SDK configurado)
   - Yarn ou npm

2. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/zen-launcher.git
   cd zen-launcher
   ```

3. **Instale as dependências:**
   ```bash
   yarn install
   # ou
   npm install
   ```

4. **Execute o script de configuração:**
   ```bash
   ./setup_project_structure.sh
   ```

5. **Configure as variáveis de ambiente necessárias:**
   Crie um arquivo `.env` na raiz do projeto:
   ```
   WEATHER_API_KEY=sua_chave_da_api_do_clima
   ```

### Executando o Projeto

1. **Inicie o Metro Bundler:**
   ```bash
   yarn start
   # ou
   npm start
   ```

2. **Execute no Android:**
   ```bash
   yarn android
   # ou
   npm run android
   ```

## Estrutura do Projeto

O projeto segue uma arquitetura modular com separação clara de responsabilidades:

- **src/app**: Configuração central da aplicação
- **src/core**: Componentes e serviços compartilhados
- **src/modules**: Módulos de funcionalidades específicas
- **src/navigation**: Configuração de navegação
- **src/state**: Gerenciamento de estado global
- **src/types**: Tipos TypeScript globais

Cada módulo contém suas próprias pastas de componentes, telas e hooks.

## Diretrizes de Codificação

### Estilo de Código

- Siga as diretrizes do ESLint e Prettier configuradas no projeto
- Use TypeScript para todas as novas implementações
- Mantenha os componentes pequenos e com responsabilidade única
- Siga as convenções de nomenclatura existentes

### Componentes React

- Use componentes funcionais com hooks
- Extraia lógica complexa para hooks personalizados
- Utilize o TailwindCSS/NativeWind para estilização
- Mantenha a responsividade para diferentes tamanhos de tela

### Estado e Side Effects

- Use o Redux para estado global compartilhado entre componentes
- Use o useState e useReducer para estado local
- Use o useEffect para efeitos colaterais, com dependências apropriadas
- Implemente limpeza apropriada nos useEffects quando necessário

## Processo de Contribuição

### Fluxo de Trabalho

1. **Crie uma branch para sua feature ou correção:**
   ```bash
   git checkout -b feature/nome-da-feature
   # ou
   git checkout -b fix/nome-do-bug
   ```

2. **Faça suas alterações com commits significativos:**
   ```bash
   git commit -m "feat: adiciona funcionalidade X"
   git commit -m "fix: corrige problema Y"
   ```

3. **Envie para o GitHub:**
   ```bash
   git push origin sua-branch
   ```

4. **Abra um Pull Request (PR)**

### Convenções de Commit

Seguimos convenções de commit semânticas:

- `feat`: nova funcionalidade
- `fix`: correção de bug
- `docs`: alterações na documentação
- `style`: formatação, faltando ponto e vírgula, etc.
- `refactor`: refatoração de código
- `perf`: melhorias de performance
- `test`: adicionando ou corrigindo testes
- `chore`: alterações no processo de build, ferramentas, etc.

### Pull Requests

- Descreva claramente o objetivo das alterações
- Referencie issues relacionadas
- Garanta que todos os testes estejam passando
- Solicite revisão de pelo menos um colaborador

## Testes

### Executando Testes

```bash
yarn test
# ou
npm test
```

### Escrevendo Testes

- Escreva testes para novas funcionalidades e correções
- Use Jest para testes unitários
- Use React Testing Library para testes de componentes

## Deploy

### Gerando uma Build para a Play Store

1. Incremente a versão no arquivo `package.json`
2. Execute:
   ```bash
   yarn build:bundle
   # ou
   npm run build:bundle
   ```
3. O arquivo AAB gerado estará em `android/app/build/outputs/bundle/release/`

## Recursos Adicionais

- [Documentação do React Native](https://reactnative.dev/docs/getting-started)
- [Documentação do Redux Toolkit](https://redux-toolkit.js.org/)
- [Documentação do React Navigation](https://reactnavigation.org/docs/getting-started)
- [Documentação do TailwindCSS](https://tailwindcss.com/docs)

## Código de Conduta

Por favor, leia nosso [Código de Conduta](CODE_OF_CONDUCT.md) antes de contribuir com o projeto.
EOF