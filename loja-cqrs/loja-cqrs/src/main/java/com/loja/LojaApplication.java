package com.loja;

import com.loja.api.PedidoController;
import com.loja.command.AtualizarStatusPedidoCommand;
import com.loja.command.AtualizarStatusPedidoHandler;
import com.loja.command.CriarPedidoCommand;
import com.loja.command.CriarPedidoHandler;
import com.loja.command.PedidoWriteRepository;
import com.loja.event.PedidoCriadoEvent;
import com.loja.event.StatusPedidoAtualizadoEvent;
import com.loja.infra.EventBus;
import com.loja.infra.Mediator;
import com.loja.query.ConsultarFaturamentoHandler;
import com.loja.query.ConsultarFaturamentoQuery;
import com.loja.query.ConsultarPedidoHandler;
import com.loja.query.ConsultarPedidoQuery;
import com.loja.query.ListarPedidosPorClienteHandler;
import com.loja.query.ListarPedidosPorClienteQuery;
import com.loja.query.PedidoProjection;
import com.loja.query.PedidoReadStore;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Ponto de entrada e "fiação" do sistema: cria os componentes e liga cada um aos conectores.
 *
 * Uso:
 *   (sem argumentos)  executa a demonstração roteirizada
 *   interativo        abre um menu para testar à mão
 */
public class LojaApplication {

    /** Atraso simulado do broker. É o que torna visível a consistência eventual. */
    private static final long LATENCIA_BROKER_MS = 400;

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // Conectores
        Mediator mediator = new Mediator();
        EventBus eventBus = new EventBus(LATENCIA_BROKER_MS);

        // Lado de escrita: Write DB + Command Handlers
        PedidoWriteRepository writeDb = new PedidoWriteRepository();
        mediator.registerCommand(CriarPedidoCommand.class, new CriarPedidoHandler(writeDb, eventBus));
        mediator.registerCommand(AtualizarStatusPedidoCommand.class, new AtualizarStatusPedidoHandler(writeDb, eventBus));

        // Lado de leitura: Read DB + Query Handlers
        PedidoReadStore readDb = new PedidoReadStore();
        mediator.registerQuery(ConsultarPedidoQuery.class, new ConsultarPedidoHandler(readDb));
        mediator.registerQuery(ListarPedidosPorClienteQuery.class, new ListarPedidosPorClienteHandler(readDb));
        mediator.registerQuery(ConsultarFaturamentoQuery.class, new ConsultarFaturamentoHandler(readDb));

        // Sincronização: a projeção assina os eventos do lado de escrita
        PedidoProjection projecao = new PedidoProjection(readDb);
        eventBus.subscribe(PedidoCriadoEvent.class, projecao::aoCriarPedido);
        eventBus.subscribe(StatusPedidoAtualizadoEvent.class, projecao::aoAtualizarStatus);

        // Entrada
        PedidoController controller = new PedidoController(mediator);

        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("interativo")) {
                new ModoInterativo(controller).executar();
            } else {
                new Demonstracao(controller, eventBus).executar();
            }
        } finally {
            eventBus.encerrar();
        }
    }
}
