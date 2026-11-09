package com.loja.query;

import org.springframework.stereotype.Component;
import com.loja.infra.Log;
import com.loja.infra.QueryHandler;

import java.util.Optional;

@Component
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
