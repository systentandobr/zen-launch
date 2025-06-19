# MindfulLauncher

![Status do Projeto](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Linguagem](https://img.shields.io/badge/kotlin-1.9.0-blue)
![Licença](https://img.shields.io/badge/licença-MIT-green)

Um launcher Android minimalista focado em bem-estar digital e produtividade, que ajuda a reduzir o uso excessivo de aplicativos distrativas.

## Visão Geral

O MindfulLauncher é um launcher Android que combina:

- **Design Minimalista**: Interface limpa e sem distrações
- **Deep Focus Mode**: Bloqueio temporário de aplicativos distrativas
- **Monitoramento de Tempo**: Acompanhamento do tempo gasto em cada aplicativo
- **Modo Standby/Always On**: Interface especial durante o carregamento

## Funcionalidades Implementadas

- 🏠 **Home Screen Minimalista**: Tela inicial com relógio grande e acesso fácil a aplicativos favoritos
- 📊 **Monitoramento de Uso**: Dashboard detalhada sobre seu tempo de tela, com estatísticas diárias, semanais e mensais
- 🚫 **Bloqueio de Aplicativos**: Sistema flexível para bloquear aplicativos por períodos determinados, com diferentes níveis de rigor
- ⏰ **Lembretes e Notificações**: Alertas quando você atinge limites de tempo predefinidos
- 🌙 **Modo Standby**: Tela de relógio elegante que aparece automaticamente quando seu dispositivo está carregando
- 🧠 **Arquitetura Robusta**: Desenvolvido com princípios SOLID e Clean Architecture para fácil expansão e manutenção

## Capturas de Tela

*(Imagens serão adicionadas em breve)*

## Tecnologias Utilizadas

- **Kotlin**: Linguagem oficial para desenvolvimento Android
- **MVVM + Clean Architecture**: Arquitetura escalável e testável
- **Coroutines + Flow**: Para operações assíncronas e reativas
- **Room**: Para persistência de dados locais
- **Hilt**: Para injeção de dependência eficiente
- **Navigation Component**: Para navegação entre telas
- **ViewBinding**: Para acesso seguro a elementos da UI
- **Material Design**: Para componentes de UI modernos

## Requisitos

- Android 6.0 (API 24) ou superior
- Permissões necessárias:
  - QUERY_ALL_PACKAGES: Para listar todos os aplicativos instalados
  - PACKAGE_USAGE_STATS: Para monitorar o tempo de uso de aplicativos
  - FOREGROUND_SERVICE: Para monitoramento em segundo plano
  - RECEIVE_BOOT_COMPLETED: Para iniciar automaticamente após reinicialização

## Instalação para Desenvolvedores

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/MindfulLauncher.git
```

2. Abra o projeto no Android Studio

3. Execute em um dispositivo ou emulador com Android 6.0+

## Estrutura do Projeto

O projeto segue a arquitetura Clean Architecture com MVVM:

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/mindfulauncher/
│   │   │   ├── data/            # Implementação de repositórios, fontes de dados
│   │   │   ├── domain/          # Entidades, casos de uso, interfaces
│   │   │   ├── presentation/    # Activities, Fragments, ViewModels
│   │   │   └── di/              # Módulos de injeção de dependência
│   │   └── res/                 # Recursos de UI
│   └── test/                    # Testes unitários
├── docs/                        # Documentação adicional
└── gradle/                      # Configuração do Gradle
```

## Roadmap

- [ ] Personalização com temas claros e escuros
- [ ] Perfis de uso (trabalho, lazer, foco)
- [ ] Widgets personalizáveis na tela inicial
- [ ] Análises avançadas de uso e sugestões
- [ ] Sincronização entre dispositivos
- [ ] Integração com calendário para bloqueios automáticos

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.

## Contribuição

Contribuições são bem-vindas! Por favor, consulte o arquivo CONTRIBUTING.md para diretrizes.

---

*Simplifique sua vida digital, um minuto de cada vez.*
