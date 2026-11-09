package com.loja.query;

import com.loja.infra.Query;

import java.util.Optional;

public record ConsultarPedidoQuery(long pedidoId) implements Query<Optional<PedidoView>> {
}
