# GitHub Actions Workflows

Este diretório contém os workflows do GitHub Actions configurados para este projeto.

## Workflows Disponíveis

### CI (`ci.yml`)
- Executa em pushes para `main` e `develop` e em pull requests para essas branches
- Realiza lint e type checking

### Expo Preview (`expo-preview.yml`)
- Executa em pull requests para `main`
- Cria uma build de preview usando EAS

### Production Build (`production.yml`)
- Executa em pushes para `main`
- Cria uma build de produção usando EAS

## Configuração Necessária

Para os workflows do Expo e EAS funcionarem, você precisa configurar o seguinte secret no GitHub:

- `EXPO_TOKEN`: Um token de acesso da sua conta Expo. Você pode gerar um em https://expo.dev/accounts/[your-username]/settings/access-tokens.

## Como adicionar o secret EXPO_TOKEN

1. Vá para a página do seu repositório no GitHub
2. Clique em "Settings" > "Secrets and variables" > "Actions"
3. Clique em "New repository secret"
4. Nome: EXPO_TOKEN
5. Valor: Seu token de acesso do Expo
6. Clique em "Add secret"
