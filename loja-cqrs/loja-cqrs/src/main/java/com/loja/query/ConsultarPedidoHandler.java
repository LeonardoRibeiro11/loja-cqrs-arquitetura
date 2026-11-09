package com.loja.query;

import com.loja.infra.Log;
import com.loja.infra.QueryHandler;

import java.util.Optional;

/** QUERY HANDLER: busca direto no Read DB, sem passar pelo domínio e sem validar regras. */
public class ConsultarPedidoHandler implements QueryHandler<ConsultarPedidoQuery, Optional<PedidoView>> {

    private final PedidoReadStore readDb;

    public ConsultarPedidoHandler(PedidoReadStore readDb) {
        this.readDb = readDb;
    }

    @Override
    public Optional<PedidoView> handle(ConsultarPedidoQuery consulta) {
        Log.step("READ DB", "consulta direta do pedido #" + consulta.pedidoId());
        return readDb.buscar(consulta.pedidoId());
    }
}
