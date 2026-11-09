# Loja CQRS: sistema de pedidos com CQRS

Exemplo prático do seminário de Padrões Arquiteturais. É um sistema de pedidos de uma loja virtual que usa o padrão **CQRS** (Command Query Responsibility Segregation) na variação com **dois bancos** (escrita e leitura) sincronizados por **eventos**.

Funcionalidades:

| Funcionalidade | Lado | Mensagem |
|---|---|---|
| Cadastrar pedido | Escrita | `CriarPedidoCommand` |
| Atualizar status do pedido | Escrita | `AtualizarStatusPedidoCommand` |
| Consultar pedido | Leitura | `ConsultarPedidoQuery` |
| Listar pedidos por cliente | Leitura | `ListarPedidosPorClienteQuery` |
| Consultar faturamento | Leitura | `ConsultarFaturamentoQuery` |

Escrito em **Java puro (17 ou superior), sem frameworks e sem dependências**, para que os conectores (Mediator e Event Bus) apareçam no código em vez de ficarem escondidos dentro de uma biblioteca.

## Como executar

```bash
./run.sh              # demonstração roteirizada (8 etapas)
./run.sh interativo   # menu para testar à mão
```

`run.sh` compila com `javac` e executa. Funciona no Linux e no WSL. No Windows sem WSL, ou no IntelliJ: abra a pasta, marque `src/main/java` como *Sources Root* (ou abra como projeto Maven, usando o `pom.xml`) e execute a classe `com.loja.LojaApplication`. Para o menu, passe o argumento `interativo`.

## Estrutura de pastas

```
loja-cqrs/
  run.sh
  pom.xml                  (opcional, só para abrir no IntelliJ como projeto Maven)
  src/main/java/com/loja/
    LojaApplication.java   monta os componentes e liga aos conectores
    Demonstracao.java      roteiro da demonstração
    ModoInterativo.java    menu de console
    api/
      PedidoController.java       ENTRADA: converte chamadas em Command ou Query
    command/                      LADO DE ESCRITA
      CriarPedidoCommand.java
      AtualizarStatusPedidoCommand.java
      CriarPedidoHandler.java
      AtualizarStatusPedidoHandler.java
      PedidoWriteRepository.java  Write DB (simulado em memória)
    domain/                       DOMAIN MODEL (usado só pela escrita)
      Pedido.java  ItemPedido.java  StatusPedido.java  RegraDeNegocioException.java
    event/                        CONTRATO entre escrita e leitura
      PedidoCriadoEvent.java  StatusPedidoAtualizadoEvent.java
    query/                        LADO DE LEITURA
      ConsultarPedidoQuery.java  ListarPedidosPorClienteQuery.java  ConsultarFaturamentoQuery.java
      ConsultarPedidoHandler.java  ListarPedidosPorClienteHandler.java  ConsultarFaturamentoHandler.java
      PedidoView.java  FaturamentoView.java   Read Models (DTOs)
      PedidoReadStore.java                    Read DB (simulado em memória)
      PedidoProjection.java                   Event Consumer: atualiza o Read DB
    infra/                        CONECTORES e utilitários
      Mediator.java   (Command Bus + Query Bus)
      EventBus.java   (broker assíncrono, publish/subscribe)
      Command.java  Query.java  CommandHandler.java  QueryHandler.java  DomainEvent.java
      Log.java  Formato.java
```

## Componentes arquiteturais, e onde estão no código

| Componente (seminário) | Classe(s) |
|---|---|
| UI / Cliente / API (Controller) | `PedidoController` (a UI é o console: `Demonstracao` e `ModoInterativo`) |
| Command | `CriarPedidoCommand`, `AtualizarStatusPedidoCommand` |
| Command Handler | `CriarPedidoHandler`, `AtualizarStatusPedidoHandler` |
| Domain Model | `Pedido`, `ItemPedido`, `StatusPedido` |
| Write Database | `PedidoWriteRepository` |
| Domain Event | `PedidoCriadoEvent`, `StatusPedidoAtualizadoEvent` |
| Event Consumer (Projection) | `PedidoProjection` |
| Read Database | `PedidoReadStore` |
| Read Model / DTO | `PedidoView`, `FaturamentoView` |
| Query | `ConsultarPedidoQuery`, `ListarPedidosPorClienteQuery`, `ConsultarFaturamentoQuery` |
| Query Handler | `ConsultarPedidoHandler`, `ListarPedidosPorClienteHandler`, `ConsultarFaturamentoHandler` |

## Conectores

| Conector | Classe | Natureza | Liga |
|---|---|---|---|
| Command Bus / Query Bus (Mediator) | `Mediator` | Em memória, síncrono (requisição e resposta) | Controller → Handlers |
| Message Broker / Event Bus | `EventBus` | Assíncrono, publish/subscribe | Command Handlers → `PedidoProjection` |
| Acesso a dados | `PedidoWriteRepository`, `PedidoReadStore` | Chamada direta (no sistema real: ORM / SQL / driver) | Handlers e projeção → bancos |
| Chamada de método | `LojaApplication` (fiação) | Síncrono | Console → Controller |

## Fluxos

**Escrita:** `PedidoController` → `Mediator.send` → Command Handler → `Pedido` (valida as regras) → `PedidoWriteRepository` → `EventBus.publish`.

**Sincronização (assíncrona):** `EventBus` → `PedidoProjection` → `PedidoReadStore`. O atraso simulado é de 400 ms (constante `LATENCIA_BROKER_MS` em `LojaApplication`).

**Leitura:** `PedidoController` → `Mediator.ask` → Query Handler → `PedidoReadStore` → `PedidoView`. Não passa pelo domínio.

Regra de dependência (conferida no código): o pacote `query` **não importa** `domain` nem `command`, e `command` e `domain` **não importam** `query`. Os dois lados só se comunicam pelos **eventos** (pacote `event`), por isso o status viaja como texto nos eventos.

## Roteiro de apresentação (o que o professor pediu)

1. **Como o código implementa o padrão:** escrita e leitura usam modelos, classes e bancos diferentes (`Pedido` / `PedidoWriteRepository` de um lado, `PedidoView` / `PedidoReadStore` do outro), ligados por eventos.
2. **Estrutura de pastas:** mostrar a árvore acima. As pastas `command` e `query` são os dois lados, `domain` fica do lado da escrita, `infra` guarda os conectores.
3. **Componentes:** usar a tabela de componentes.
4. **Conectores:** `Mediator` (síncrono) e `EventBus` (assíncrono). O `EventBus` é o que causa a consistência eventual.
5. **Execução:** rodar `./run.sh`. Pontos para comentar na saída:
   - etapa 2: a consulta logo depois da escrita ainda não encontra o pedido (**consistência eventual**);
   - etapa 4: depois da sincronização, o pedido aparece;
   - etapa 6: o faturamento (R$ 1.310,00) é lido já calculado (**Read Model**);
   - etapa 7: o domínio rejeita `CRIADO → ENTREGUE` e nenhum evento é publicado;
   - etapa 8: cancelar um pedido pago reduz o faturamento (R$ 900,00).

## Regras de negócio do domínio

- Cliente e ao menos um item são obrigatórios. Quantidade e preço devem ser maiores que zero.
- Transições de status permitidas: `CRIADO → PAGO | CANCELADO`, `PAGO → ENVIADO | CANCELADO`, `ENVIADO → ENTREGUE`. `ENTREGUE` e `CANCELADO` são estados finais.
- O faturamento soma os pedidos em `PAGO`, `ENVIADO` e `ENTREGUE`.

## O que foi simplificado (e o que seria diferente em um sistema real)

- **Bancos em memória:** `PedidoWriteRepository` e `PedidoReadStore` são mapas em memória. No sistema real, seriam, por exemplo, PostgreSQL (escrita) e uma tabela desnormalizada, NoSQL ou cache (leitura). Os dados somem ao encerrar o programa.
- **Broker simulado:** o `EventBus` usa uma thread com atraso fixo no lugar de RabbitMQ ou Kafka. Ele preserva a ordem dos eventos, mas não tem persistência nem reentrega.
- **Sem Transactional Outbox:** o handler salva no Write DB e depois publica o evento. Se o programa caísse entre as duas ações, o evento seria perdido (problema do *dual write*). Em produção, o evento seria gravado na mesma transação do estado (Outbox).
- **Idempotência:** a projeção ignora eventos repetidos ou antigos usando a `versao` do pedido.
- **Sem framework:** em Spring Boot, o `PedidoController` viraria um `@RestController`, os handlers seriam `@Service`, o `Mediator` seria substituído por injeção de dependência (ou uma biblioteca de mediator) e o `EventBus` por `ApplicationEventPublisher` ou um broker de verdade.
