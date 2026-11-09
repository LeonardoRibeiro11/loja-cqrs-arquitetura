package com.loja.command;

import com.loja.domain.Pedido;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WRITE DATABASE (simulado em memória). Guarda o agregado Pedido completo, com todas as regras.
 * Em um sistema real seria um banco relacional transacional (ACID), acessado por ORM.
 */
public class PedidoWriteRepository {

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    public long proximoId() {
        return sequencia.incrementAndGet();
    }

    public void salvar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    public Optional<Pedido> buscarPorId(long id) {
        return Optional.ofNullable(pedidos.get(id));
    }
}
