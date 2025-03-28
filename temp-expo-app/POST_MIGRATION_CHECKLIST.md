# Checklist Pós-Migração

Use este checklist para garantir que sua migração do React Native para o Expo esteja completa e funcionando corretamente.

## Ambiente de Desenvolvimento

- [ ] Verifique se o servidor de desenvolvimento inicia sem erros: `npm start`
- [ ] Confirme se o aplicativo é executado no iOS
- [ ] Confirme se o aplicativo é executado no Android
- [ ] Verifique se o Hot Reload está funcionando

## Funcionalidades

- [ ] Navegação - Todas as rotas funcionam
- [ ] Formulários - Campos e validações funcionam
- [ ] APIs - Chamadas de API funcionam corretamente
- [ ] Armazenamento local - Dados são salvos e recuperados corretamente
- [ ] Permissões - Solicitações de permissão funcionam
- [ ] Recursos específicos de plataforma - Funcionam em ambos iOS e Android

## Módulos Nativos

- [ ] Substitua todos os módulos nativos não suportados por equivalentes do Expo
- [ ] Configure os plugins necessários no app.json
- [ ] Verifique se cada funcionalidade nativa está operando corretamente

## UI e Experiência do Usuário

- [ ] As fontes estão carregando corretamente
- [ ] Imagens e ícones são exibidos corretamente
- [ ] Gestos e animações funcionam como esperado
- [ ] A navegação possui transições suaves
- [ ] A responsividade em diferentes tamanhos de tela

## Desempenho

- [ ] O tempo de inicialização é aceitável
- [ ] As animações são suaves
- [ ] O uso de memória é razoável
- [ ] O consumo de bateria não é excessivo

## Testes

- [ ] Testes unitários executam sem erros: `npm test`
- [ ] Snapshots estão atualizados
- [ ] Cobertura de testes é satisfatória

## Construção (Build)

- [ ] Configuração do EAS está correta
- [ ] Build de desenvolvimento funciona: `npm run eas-build:dev`
- [ ] Build de preview funciona: `npm run eas-build:preview` 
- [ ] Build de produção funciona: `npm run eas-build:prod`

## Próximos Passos

- [ ] Atualize as informações no app.json (nome, versão, ID)
- [ ] Configure atualizações OTA com o EAS Update
- [ ] Configure notificações push
- [ ] Configure análise de crashes
- [ ] Configure o GitHub Actions para CI/CD
