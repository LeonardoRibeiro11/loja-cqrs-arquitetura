package com.loja.query;

import org.springframework.stereotype.Component;
import com.loja.infra.Log;
import com.loja.infra.QueryHandler;

import java.util.List;

@Component
public class ListarPedidosPorClienteHandler implements QueryHandler<ListarPedidosPorClienteQuery, List<PedidoView>> {

    private final PedidoReadStore readDb;

    public ListarPedidosPorClienteHandler(PedidoReadStore readDb) {
        this.readDb = readDb;
    }

    @Override
    public List<PedidoView> handle(ListarPedidosPorClienteQuery consulta) {
        Log.step("READ DB", "consulta direta dos pedidos do cliente " + consulta.clienteId());
        return readDb.listarPorCliente(consulta.clienteId());
    }
}
