# React Native vs Expo

## Principais Diferenças

| Aspecto | React Native Puro | Expo |
|---------|-------------------|------|
| **Setup inicial** | Mais complexo, requer configuração manual | Mais simples, configuração automatizada |
| **Código nativo** | Acesso total ao código nativo | Acesso limitado (com EAS, acesso via plugins) |
| **Dependências nativas** | Liberdade para usar qualquer biblioteca | Limitado às bibliotecas suportadas pelo Expo |
| **Compilação** | Compilação local usando Xcode/Android Studio | Compilação na nuvem usando EAS Build |
| **OTA Updates** | Requer solução personalizada | Integrado via EAS Update |
| **Configuração** | Arquivos de configuração separados por plataforma | Configuração centralizada via app.json |
| **Desenvolvimento** | Requer mais conhecimento de iOS/Android | Menos conhecimento nativo necessário |

## Vantagens do Expo

1. **Velocidade de desenvolvimento**: Setup mais rápido e iteração mais ágil
2. **Atualizações Over-the-Air**: Atualizações sem passar pelas lojas
3. **EAS Build**: Builds na nuvem sem necessidade de configurar ambiente local
4. **Simplicidade**: Menos código de configuração, mais foco na aplicação
5. **Manutenção**: Atualizações de SDK mais simples e confiáveis
6. **SDK unificado**: Módulos nativos pré-configurados

## Limitações do Expo

1. **Acesso nativo limitado**: Mesmo com os plugins, algumas funcionalidades nativas exigem workarounds
2. **Tamanho do aplicativo**: Apps Expo tendem a ser maiores devido às bibliotecas incluídas
3. **Controle reduzido**: Menos flexibilidade em algumas configurações nativas
4. **Dependência da infraestrutura Expo**: Para builds e updates
5. **Possíveis atrasos em novas APIs nativas**: Tempo para incorporar novos recursos do iOS/Android

## Quando usar Expo

- Startups e MVPs que precisam de desenvolvimento rápido
- Equipes com menos experiência em desenvolvimento móvel nativo
- Aplicativos que não precisam de integrações nativas muito específicas
- Quando a velocidade de atualização é crucial (OTA updates)

## Quando usar React Native puro

- Aplicativos que precisam de acesso a APIs nativas não suportadas pelo Expo
- Projetos que requerem otimizações específicas de performance
- Quando é necessário controle total sobre o código nativo
- Integração com SDKs nativos de terceiros não suportados pelo Expo

## O melhor dos dois mundos: Expo com EAS

Com o Expo Application Services (EAS), muitas das limitações tradicionais do Expo foram reduzidas:

- **EAS Build**: Permite configurações nativas mais avançadas
- **Config Plugins**: Permite modificar código nativo sem ejetar
- **Desenvolvimento com cliente personalizado**: Para testar módulos nativos personalizados
