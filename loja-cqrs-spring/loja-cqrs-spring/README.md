# Loja CQRS (versão Spring Boot)

Mesmo sistema de pedidos da versão em Java puro, com a **entrada REST do Spring Boot** e injeção de dependências. O núcleo (domínio, eventos, handlers, projeção e os conectores `Mediator` e `EventBus`) é o mesmo código da versão em Java puro.

## Como executar

Requisitos: Java 17 ou superior e Maven (ou o Maven embutido no IntelliJ).

- **IntelliJ:** abra a pasta (ou o `pom.xml`) como projeto Maven e execute `com.loja.LojaApplication`.
- **Terminal:** `mvn spring-boot:run`

A aplicação sobe em `http://localhost:8080`. Com ela rodando, em outro terminal (Linux, WSL ou Git Bash):

```bash
./demo.sh
```

O `demo.sh` percorre os mesmos passos da demonstração em Java puro, por HTTP. Os logs de cada componente (`[COMMAND BUS]`, `[DOMAIN MODEL]`, `[WRITE DB]`, `[EVENT BUS]`, `[PROJECTION]`, `[READ DB]`) aparecem no **console da aplicação**.

## Endpoints

| Método e caminho | Lado | Resposta |
|---|---|---|
| `POST /pedidos` | Escrita | `201 Created` com `{"id": n}` |
| `PUT /pedidos/{id}/status` | Escrita | `202 Accepted` (a leitura é atualizada de forma assíncrona) |
| `GET /pedidos/{id}` | Leitura | `200`, ou `404` se o pedido ainda não chegou ao banco de leitura |
| `GET /pedidos?clienteId=ana` | Leitura | `200` com a lista |
| `GET /faturamento` | Leitura | `200` |

Erros de negócio voltam como `422` (ex.: `CRIADO → ENTREGUE`) e pedido inexistente em `PUT` como `404`, sempre com `{"erro": "mensagem"}`.

Exemplos:

```bash
curl -X POST localhost:8080/pedidos -H "Content-Type: application/json" \
  -d '{"clienteId":"Ana","itens":[{"produto":"Teclado","quantidade":1,"precoUnitario":250.00}]}'

curl -X PUT localhost:8080/pedidos/1/status -H "Content-Type: application/json" -d '{"status":"PAGO"}'

curl localhost:8080/pedidos/1
curl "localhost:8080/pedidos?clienteId=ana"
curl localhost:8080/faturamento
```

No PowerShell, use `Invoke-RestMethod` (ou o Postman) com os mesmos corpos JSON.

## O que mudou em relação à versão em Java puro

| Arquivo | Mudança |
|---|---|
| `LojaApplication` | Passa a ser `@SpringBootApplication` |
| `api/PedidoController` | Passa a ser `@RestController`, com os endpoints acima |
| `api/ApiExceptionHandler`, `CriarPedidoRequest`, `AtualizarStatusRequest` | Novos: tratamento de erros e corpos JSON |
| `config/CqrsConfig` | Nova: cria `Mediator` e `EventBus` e liga handlers e projeção |
| Handlers, repositórios e `PedidoProjection` | Ganham `@Component` ou `@Repository` |
| `domain/PedidoNaoEncontradoException` | Nova (vira HTTP 404) |
| `Demonstracao`, `ModoInterativo`, `run.sh` | Removidos (a interface agora é HTTP) |

`domain`, `event` e `infra` **não dependem do Spring**. Os conectores continuam sendo as classes `Mediator` e `EventBus`: aqui o Spring só injeta e configura.

## Observações

- A versão do Spring Boot no `pom.xml` é a 3.5.0. Se o seu projeto usa outra versão 3.x, pode trocar sem problema. Em versões 4.x o nome de alguns starters mudou, e pode ser preciso ajustar a dependência.
- Os bancos continuam sendo mapas em memória (os dados somem ao reiniciar) e o broker continua simulado, como na versão em Java puro. Não há Outbox.
- Esta versão **não foi executada com o Spring** durante o desenvolvimento (o ambiente não baixa as dependências). A lógica, a fiação e o tratamento de erros foram exercitados chamando as classes direto; o contêiner do Spring em si (varredura de componentes, JSON, rotas HTTP) só se confirma ao subir a aplicação.
