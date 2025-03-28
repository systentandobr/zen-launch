# Configuração de Continuous Deployment (CD)

Este documento contém instruções para configurar o pipeline de deployment contínuo usando GitHub Actions para o Zen Launcher.

## Requisitos Prévios

1. **Conta Google Play Console**: Você precisa ter uma conta de desenvolvedor no Google Play Console.
2. **App cadastrado no Google Play**: O app precisa estar cadastrado e ter pelo menos uma versão interna ou alpha/beta enviada manualmente.
3. **Keystore para assinar o app**: Um arquivo keystore para assinatura do app Android.
4. **Conta no GitHub**: Com o código-fonte do app.

## Configuração de Secrets no GitHub

Para que o workflow funcione, você precisa configurar os seguintes secrets no seu repositório GitHub:

1. Vá para seu repositório no GitHub → Settings → Secrets and variables → Actions
2. Adicione os seguintes secrets:

| Nome do Secret | Descrição |
|----------------|-----------|
| `KEYSTORE_BASE64` | O conteúdo do seu arquivo keystore codificado em base64 |
| `KEYSTORE_PASSWORD` | A senha do keystore |
| `KEY_ALIAS` | O alias da chave no keystore |
| `KEY_PASSWORD` | A senha da chave |
| `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` | O conteúdo do arquivo JSON da conta de serviço do Google Play |

### Como obter esses valores

#### Keystore Base64

Para converter seu keystore em base64:

**Linux/macOS**:
```bash
base64 -i your-keystore.keystore
```

**Windows** (PowerShell):
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("your-keystore.keystore"))
```

#### Conta de Serviço do Google Play

1. Acesse o [Google Play Console](https://play.google.com/console/)
2. Vá para Setup → API access
3. Em "Service Accounts", crie ou use uma conta de serviço existente
4. Conceda as permissões necessárias (Release apps)
5. Crie uma chave JSON e baixe o arquivo
6. Copie o conteúdo completo do arquivo JSON

## Configuração do Versionamento

O workflow está configurado para usar tags git para controlar o versionamento:

- Para lançar uma versão beta: `git tag -a v1.0.0-beta.1 -m "Beta release v1.0.0-beta.1" && git push --tags`
- Para lançar uma versão de produção: `git tag -a v1.0.0 -m "Release v1.0.0" && git push --tags`

## Execução Manual do Workflow

Também é possível executar o workflow manualmente:

1. Vá para a aba "Actions" do seu repositório
2. Selecione o workflow "Android Release"
3. Clique em "Run workflow"
4. Escolha a branch e a lane desejada (beta ou production)
5. Clique em "Run workflow"

## Arquivos de Metadados (opcional)

Se quiser automatizar também o upload de metadados (descrições, imagens, screenshots), você deve configurar os diretórios de metadados do Fastlane:

```bash
cd android
fastlane supply init
```

Isso criará a estrutura de diretórios necessária. Em seguida, adicione suas descrições, imagens e screenshots nos diretórios apropriados.

## Solução de Problemas

Se o workflow falhar, verifique:

1. **Logs de erro**: Acesse os logs detalhados na aba Actions do GitHub
2. **Permissões da conta de serviço**: Verifique se a conta de serviço tem as permissões corretas no Google Play Console
3. **Versionamento**: Garanta que o versionCode seja incrementado a cada envio
4. **Keystore**: Confirme que o app está sendo assinado com a mesma chave usada para a versão original na Play Store

## Recursos Adicionais

- [Documentação do Fastlane para Android](https://docs.fastlane.tools/getting-started/android/setup/)
- [GitHub Actions para Android](https://docs.github.com/en/actions/guides/building-and-testing-java-with-gradle)
- [Google Play Developer API](https://developers.google.com/android-publisher)
