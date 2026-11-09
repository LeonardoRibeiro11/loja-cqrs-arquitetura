package com.loja.query;

import com.loja.infra.Query;

import java.util.List;

/** Consulta: pedidos de um cliente. */
public record ListarPedidosPorClienteQuery(String clienteId) implements Query<List<PedidoView>> {
}
