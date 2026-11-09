package com.loja.command;

import org.springframework.stereotype.Component;
import com.loja.domain.Pedido;
import com.loja.domain.PedidoNaoEncontradoException;
import com.loja.event.StatusPedidoAtualizadoEvent;
import com.loja.infra.CommandHandler;
import com.loja.infra.EventBus;
import com.loja.infra.Log;

@Component
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
                .orElseThrow(() -> new PedidoNaoEncontradoException(comando.pedidoId()));

        // Duas requisições simultâneas no mesmo pedido não podem se misturar: o bloco abaixo é atômico por pedido.
        // Publicar o evento dentro do bloco também mantém os eventos do pedido na ordem das versões.
        synchronized (pedido) {
            String statusAnterior = pedido.getStatus().name();
            pedido.atualizarStatus(comando.novoStatus()); // a regra de transição mora no domínio
            Log.step("DOMAIN MODEL", "transição " + statusAnterior + " -> " + pedido.getStatus() + " aceita");

            writeDb.salvar(pedido);
            Log.step("WRITE DB", "pedido #" + pedido.getId() + " atualizado");

            eventBus.publish(new StatusPedidoAtualizadoEvent(pedido.getId(), pedido.getVersao(), statusAnterior,
                    pedido.getStatus().name(), pedido.total()));
        }
        return null;
    }
}
