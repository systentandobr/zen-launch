#!/bin/bash

# Remover arquivos de lock e node_modules
echo "Removendo arquivos de lock e node_modules..."
rm -f package-lock.json
rm -f pnpm-lock.yaml
rm -rf node_modules

# Instalar dependências
echo "Instalando dependências..."
pnpm install

# Verificar se a instalação foi bem-sucedida
if [ $? -eq 0 ]; then
  echo "Instalação concluída com sucesso!"
  echo "Agora você pode executar: npm run web"
else
  echo "Falha na instalação. Verifique os erros acima."
fi
