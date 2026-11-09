#!/usr/bin/env bash
# Compila e executa o projeto. Requer Java 17 ou superior.
#   ./run.sh              demonstração roteirizada
#   ./run.sh interativo   menu para testar à mão
set -e
cd "$(dirname "$0")"
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out com.loja.LojaApplication "$@"
