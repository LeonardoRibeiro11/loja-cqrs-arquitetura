package com.loja.command;

import org.springframework.stereotype.Repository;
import com.loja.domain.Pedido;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
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
