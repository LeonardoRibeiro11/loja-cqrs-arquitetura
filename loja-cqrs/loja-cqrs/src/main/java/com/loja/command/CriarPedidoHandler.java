package com.loja.command;

import com.loja.domain.Pedido;
import com.loja.event.PedidoCriadoEvent;
import com.loja.infra.CommandHandler;
import com.loja.infra.EventBus;
import com.loja.infra.Log;

/** COMMAND HANDLER: orquestra o caso de uso "cadastrar pedido". */
public class CriarPedidoHandler implements CommandHandler<CriarPedidoCommand, Long> {

    private final PedidoWriteRepository writeDb;
    private final EventBus eventBus;

    public CriarPedidoHandler(PedidoWriteRepository writeDb, EventBus eventBus) {
        this.writeDb = writeDb;
        this.eventBus = eventBus;
    }

    @Override
    public Long handle(CriarPedidoCommand comando) {
        // 1. Domain Model valida as regras de negócio
        Pedido pedido = Pedido.criar(writeDb.proximoId(), comando.clienteId(), comando.itens());
        Log.step("DOMAIN MODEL", "regras validadas para o pedido #" + pedido.getId());

        // 2. Persiste o estado no banco de escrita
        writeDb.salvar(pedido);
        Log.step("WRITE DB", "pedido #" + pedido.getId() + " salvo (status " + pedido.getStatus() + ")");

        // 3. Publica o fato ocorrido para o lado de leitura
        eventBus.publish(new PedidoCriadoEvent(pedido.getId(), pedido.getVersao(), pedido.getClienteId(),
                pedido.getItens().size(), pedido.total(), pedido.getStatus().name()));

        return pedido.getId();
    }
}
