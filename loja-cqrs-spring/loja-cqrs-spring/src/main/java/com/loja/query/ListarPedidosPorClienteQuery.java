package com.loja.query;

import com.loja.infra.Query;

import java.util.List;

public record ListarPedidosPorClienteQuery(String clienteId) implements Query<List<PedidoView>> {
}
