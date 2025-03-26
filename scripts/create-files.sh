#!/bin/bash

# Verificar se foram fornecidos os argumentos corretos
if [ $# -ne 3 ]; then
    echo "Uso: $0 <prefixo> <índice_inicial> <índice_final>"
    echo "Exemplo: $0 script 1 10 (Cria arquivos de script1.sh até script10.sh)"
    exit 1
fi

# Atribuir os argumentos a variáveis
PREFIX=$1
START=$2
END=$3

# Verificar se os índices são números inteiros
if ! [[ "$START" =~ ^[0-9]+$ ]] || ! [[ "$END" =~ ^[0-9]+$ ]]; then
    echo "Erro: Os índices inicial e final devem ser números inteiros."
    exit 1
fi

# Verificar se o índice inicial é menor ou igual ao índice final
if [ "$START" -gt "$END" ]; then
    echo "Erro: O índice inicial deve ser menor ou igual ao índice final."
    exit 1
fi

# Criar os arquivos
for ((i=START; i<=END; i++)); do
    FILENAME="${PREFIX}${i}.sh"
    
    # Criar arquivo com permissão de execução
    touch "$FILENAME"
    chmod +x "$FILENAME"
    
    # Adicionar shebang ao arquivo
    echo '#!/bin/bash' > "$FILENAME"
    echo '' >> "$FILENAME"
    echo "# Script $i criado automaticamente" >> "$FILENAME"
    echo "echo \"Executando $FILENAME\"" >> "$FILENAME"
    
    echo "Arquivo $FILENAME criado com sucesso."
done

echo "Total de arquivos criados: $((END - START + 1))"