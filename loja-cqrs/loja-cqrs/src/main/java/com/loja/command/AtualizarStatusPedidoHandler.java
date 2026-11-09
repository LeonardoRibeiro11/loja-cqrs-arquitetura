package com.loja.command;

import com.loja.domain.Pedido;
import com.loja.domain.RegraDeNegocioException;
import com.loja.event.StatusPedidoAtualizadoEvent;
import com.loja.infra.CommandHandler;
import com.loja.infra.EventBus;
import com.loja.infra.Log;

/** COMMAND HANDLER: orquestra o caso de uso "atualizar status do pedido". */
public class AtualizarStatusPedidoHandler implements CommandHandler<AtualizarStatusPedidoCommand, Void> {

    private final PedidoWriteRepository writeDb;
    private final EventBus eventBus;

    public AtualizarStatusPedidoHandler(PedidoWriteRepository writeDb, EventBus eventBus) {
        this.writeDb = writeDb;
        this.eventBus = eventBus;
    }

    @Override
    public Void handle(AtualizarStatusPedidoCommand comando) {
        Pedido pedido = writeDb.buscarPorId(comando.pedidoId())
                .orElseThrow(() -> new RegraDeNegocioException("Pedido #" + comando.pedidoId() + " não encontrado."));

        String statusAnterior = pedido.getStatus().name();
        pedido.atualizarStatus(comando.novoStatus()); // a regra de transição mora no domínio
        Log.step("DOMAIN MODEL", "transição " + statusAnterior + " -> " + pedido.getStatus() + " aceita");

        writeDb.salvar(pedido);
        Log.step("WRITE DB", "pedido #" + pedido.getId() + " atualizado");

        eventBus.publish(new StatusPedidoAtualizadoEvent(pedido.getId(), pedido.getVersao(), statusAnterior,
                pedido.getStatus().name(), pedido.total()));
        return null;
    }
}
