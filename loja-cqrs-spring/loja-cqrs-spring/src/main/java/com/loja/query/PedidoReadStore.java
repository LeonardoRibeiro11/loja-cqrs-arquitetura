package com.loja.query;

import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Repository
public class PedidoReadStore {

    private final Map<Long, PedidoView> porId = new TreeMap<>();
    private final Map<String, Set<Long>> idsPorCliente = new HashMap<>();
    private BigDecimal faturamentoTotal = BigDecimal.ZERO.setScale(2);
    private int pedidosFaturados = 0;

    public synchronized void salvar(PedidoView view) {
        porId.put(view.id(), view);
        idsPorCliente.computeIfAbsent(view.clienteId(), k -> new TreeSet<>()).add(view.id());
    }

    public synchronized Optional<PedidoView> buscar(long id) {
        return Optional.ofNullable(porId.get(id));
    }

    public synchronized List<PedidoView> listarPorCliente(String clienteId) {
        return idsPorCliente.getOrDefault(clienteId, Set.of()).stream().map(porId::get).toList();
    }

    public synchronized void ajustarFaturamento(BigDecimal delta, int deltaPedidos) {
        faturamentoTotal = faturamentoTotal.add(delta);
        pedidosFaturados += deltaPedidos;
    }

    public synchronized FaturamentoView faturamento() {
        return new FaturamentoView(faturamentoTotal, pedidosFaturados);
    }
}
