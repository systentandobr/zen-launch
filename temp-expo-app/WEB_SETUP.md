# Configuração para Web no Expo

Este guia explica como configurar e executar o seu projeto Expo na web.

## 1. Corrigir Conflito de Dependências

No seu projeto, havia um conflito entre as versões de React e React DOM. O `react` estava na versão 18.3.1, mas o `react-dom` estava na versão 19.0.0, que exige React 19.

### Solução:
1. Edite o `package.json` para garantir que as versões sejam compatíveis:
   - `react`: 18.3.1
   - `react-dom`: 18.3.1
   - `react-test-renderer`: 18.3.1

## 2. Limpar e Reinstalar Dependências

Execute o script `clean-install.sh` para remover arquivos de lock e node_modules, e reinstalar todas as dependências:

```bash
# Tornar o script executável
chmod +x clean-install.sh

# Executar o script
./clean-install.sh
```

## 3. Executar na Web

Após a instalação bem-sucedida, você pode iniciar o aplicativo na web com:

```bash
npm run web
```

Ou use o Expo CLI diretamente:

```bash
npx expo start --web
```

## 4. Solução de Problemas Comuns

### Erro de Versão
Se você encontrar erros de versão, verifique se todas as dependências relacionadas ao React têm versões compatíveis no package.json.

### Problemas com Metro Bundler
Se o Metro Bundler falhar, tente limpar o cache:

```bash
npx expo start --clear
```

### Problemas de Porta
Se a porta padrão (19006) estiver em uso, você pode especificar uma porta diferente:

```bash
npm run web -- --port 8080
```

### Erros de Importação
Verifique se os componentes web estão corretamente importados, especialmente os específicos para web.

## 5. Desenvolvimento Web

Ao desenvolver para web, considere:

- Use `Platform.OS === 'web'` para código específico da web
- Alguns componentes nativos não funcionam na web; use alternativas
- Adapte estilos para melhor aparência na web
- Teste em diferentes navegadores

## 6. Construção para Produção

Quando estiver pronto para implantar a versão web:

```bash
npx expo export:web
```

Isso criará uma pasta `web-build` com os arquivos estáticos que podem ser hospedados em qualquer servidor web.
