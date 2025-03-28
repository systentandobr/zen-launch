# App Expo Migrado

Este projeto foi migrado de React Native puro para Expo.

## Estrutura do Projeto

```
/
├── assets/             # Imagens, fontes e outros recursos estáticos
├── src/
│   ├── components/     # Componentes React reutilizáveis
│   ├── screens/        # Telas da aplicação
│   ├── navigation/     # Configuração de navegação
│   ├── hooks/          # Custom hooks
│   ├── contexts/       # Context API providers
│   ├── services/       # Serviços (API, etc)
│   ├── utils/          # Funções utilitárias
│   ├── config/         # Configurações
│   └── types/          # Definições de tipos TypeScript
├── scripts/            # Scripts auxiliares
└── __tests__/          # Testes
```

## Começando

1. Instale as dependências:
```bash
pnpm install
```

2. Inicie o servidor de desenvolvimento:
```bash
npm start
```

3. Execute em um dispositivo ou emulador:
- Pressione `a` para executar no emulador Android
- Pressione `i` para executar no simulador iOS
- Escaneie o QR code com o aplicativo Expo Go (Android) ou Câmera (iOS)

## Scripts Disponíveis

- `npm start` - Inicia o servidor de desenvolvimento
- `npm test` - Executa os testes
- `npm run eas-build:dev` - Cria uma build de desenvolvimento usando EAS
- `npm run eas-build:preview` - Cria uma build de preview usando EAS
- `npm run eas-build:prod` - Cria uma build de produção usando EAS

## Migração

Este projeto foi migrado de React Native puro para Expo. As seguintes alterações foram feitas:

1. Substituição de módulos nativos por equivalentes do Expo
2. Configuração de plugins do Expo no app.json
3. Adaptação da estrutura de diretórios
4. Configuração de testes com jest-expo
5. Configuração de EAS para build e deploy

## Guias de Migração

Guias detalhados sobre aspectos específicos da migração estão disponíveis em:

- `source-code-migration-guide.md` - Informações sobre a migração do código-fonte
- `native-modules-migration-guide.md` - Guia para módulos nativos
- `testing-migration-guide.md` - Guia para migrar e configurar testes

## Recursos Adicionais

- [Documentação do Expo](https://docs.expo.dev/)
- [Documentação do EAS](https://docs.expo.dev/eas/)
- [Guia de migração oficial](https://docs.expo.dev/workflow/upgrading/)
