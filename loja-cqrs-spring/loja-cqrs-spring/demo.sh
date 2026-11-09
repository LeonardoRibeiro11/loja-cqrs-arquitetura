#!/usr/bin/env bash
# Demonstração por HTTP. Suba a aplicação antes (mvn spring-boot:run ou pelo IntelliJ) e rode: ./demo.sh
# Funciona melhor com a aplicação recém-iniciada (os pedidos de exemplo começam do zero).
BASE="${BASE:-http://localhost:8080}"

req() { curl -s -w "\n   -> HTTP %{http_code}\n" -H "Content-Type: application/json" "$@"; }
id_de() { sed -E 's/.*"id":([0-9]+).*/\1/'; }
titulo() { echo; echo "== $1 =="; }

titulo "1. Cadastrar pedidos (lado de ESCRITA)"
P1=$(curl -s -H "Content-Type: application/json" -X POST "$BASE/pedidos" \
  -d '{"clienteId":"Ana","itens":[{"produto":"Teclado","quantidade":1,"precoUnitario":250.00},{"produto":"Mouse","quantidade":2,"precoUnitario":80.00}]}' | id_de)
P2=$(curl -s -H "Content-Type: application/json" -X POST "$BASE/pedidos" \
  -d '{"clienteId":"Bruno","itens":[{"produto":"Monitor","quantidade":1,"precoUnitario":900.00}]}' | id_de)
P3=$(curl -s -H "Content-Type: application/json" -X POST "$BASE/pedidos" \
  -d '{"clienteId":"Ana","itens":[{"produto":"Cabo HDMI","quantidade":3,"precoUnitario":25.00}]}' | id_de)
echo "   pedidos criados: #$P1, #$P2, #$P3"

titulo "2. Consistência eventual: consulta feita ANTES de o evento chegar ao Read DB (espera 404)"
req "$BASE/pedidos/$P1"

titulo "3. Depois da sincronização (lado de LEITURA)"
sleep 1
req "$BASE/pedidos/$P1"

titulo "4. Atualizar status (lado de ESCRITA)"
req -X PUT "$BASE/pedidos/$P1/status" -d '{"status":"PAGO"}'
req -X PUT "$BASE/pedidos/$P2/status" -d '{"status":"PAGO"}'
req -X PUT "$BASE/pedidos/$P2/status" -d '{"status":"ENVIADO"}'
sleep 1

titulo "5. Listar pedidos por cliente e consultar faturamento (lado de LEITURA)"
req "$BASE/pedidos?clienteId=ana"
req "$BASE/faturamento"

titulo "6. O Domain Model protege as regras (CRIADO -> ENTREGUE deve ser rejeitado com 422)"
req -X PUT "$BASE/pedidos/$P3/status" -d '{"status":"ENTREGUE"}'

titulo "7. Cancelar um pedido pago ajusta o faturamento"
req -X PUT "$BASE/pedidos/$P1/status" -d '{"status":"CANCELADO"}'
sleep 1
req "$BASE/faturamento"
