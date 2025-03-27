#!/bin/bash

# Script 5 criado automaticamente
echo "Executando claude-ai5.sh"
cat << 'EOF' > android/fastlane/metadata/android/pt-BR/full_description.txt
Zen Launcher é um launcher para Android projetado para melhorar seu foco e produtividade, eliminando distrações e fornecendo uma interface limpa e simples.

PRINCIPAIS RECURSOS:

✓ DESIGN MINIMALISTA - Interface limpa e intuitiva que reduz o estímulo visual e as distrações.

✓ MODO FOCO - Bloqueie temporariamente o acesso a apps distrativas durante períodos específicos de trabalho ou estudo.

✓ RASTREAMENTO DE USO - Visualize estatísticas detalhadas sobre quanto tempo você gasta em cada aplicativo.

✓ GERENCIAMENTO DE TAREFAS - Lista de tarefas simples e integrada para ajudar na organização diária.

✓ PERSONALIZAÇÃO - Organize seus aplicativos em categorias personalizadas e adapte o launcher ao seu estilo.

✓ TELA DE BLOQUEIO - Proteja seu dispositivo com uma tela de bloqueio zenista com temporizador configurável.

Zen Launcher é a escolha perfeita para quem valoriza simplicidade e produtividade. Reduza as distrações digitais e recupere o controle sobre seu tempo e atenção.

Livre de anúncios e sem compras internas. Totalmente focado em sua produtividade.
EOF

cat << 'EOF' > android/fastlane/metadata/android/pt-BR/short_description.txt
Launcher Android zenista para reduzir distrações e aumentar produtividade.
EOF

cat << 'EOF' > android/fastlane/metadata/android/pt-BR/title.txt
Zen Launcher
EOF

cat << 'EOF' > android/fastlane/metadata/android/en-US/full_description.txt
Zen Launcher is an Android launcher designed to improve your focus and productivity by eliminating distractions and providing a clean, simple interface.

KEY FEATURES:

✓ MINIMALIST DESIGN - Clean and intuitive interface that reduces visual stimuli and distractions.

✓ FOCUS MODE - Temporarily block access to distracting apps during specific work or study periods.

✓ USAGE TRACKING - View detailed statistics about how much time you spend on each app.

✓ TASK MANAGEMENT - Simple integrated to-do list to help with daily organization.

✓ CUSTOMIZATION - Organize your apps into custom categories and adapt the launcher to your style.

✓ LOCK SCREEN - Protect your device with a zenist lock screen with configurable timer.

Zen Launcher is the perfect choice for those who value simplicity and productivity. Reduce digital distractions and regain control over your time and attention.

Ad-free and no in-app purchases. Fully focused on your productivity.
EOF

cat << 'EOF' > android/fastlane/metadata/android/en-US/short_description.txt
Minimalist Android launcher to reduce distractions and increase productivity.
EOF

cat << 'EOF' > android/fastlane/metadata/android/en-US/title.txt
Zen Launcher
EOF

mkdir -p android/fastlane/metadata/android/pt-BR/images/phoneScreenshots
mkdir -p android/fastlane/metadata/android/en-US/images/phoneScreenshots
mkdir -p android/fastlane/metadata/android/pt-BR/images/featureGraphic
mkdir -p android/fastlane/metadata/android/en-US/images/featureGraphic
