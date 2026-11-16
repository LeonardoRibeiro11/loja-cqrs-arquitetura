package com.loja.loja_cqrs.query;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class PedidoReadRepository {
    private final Map<Long, PedidoView> pedidosPorId = new TreeMap<>();
    private final Map<String, Set<Long >> idsPorCliente = new HashMap<>();
    private BigDecimal faturamentoTotal = BigDecimal.ZERO.setScale(2);
    private int pedidosFaturados = 0;

    public synchronized void salvar(PedidoView pedido){
        pedidosPorId.put(pedido.id(), pedido);
        idsPorCliente.computeIfAbsent(chave(pedido.clienteId()), k -> new TreeSet<>()).add(pedido.id());
    }

    public synchronized Optional<PedidoView> buscarPorId(long id){
        return Optional.ofNullable(pedidosPorId.get(id));
    }

    public synchronized List<PedidoView> listarPorCliente(String clienteId){
        return idsPorCliente.getOrDefault(chave(clienteId), Set.of()).stream()
                .map(pedidosPorId::get)
                .toList();
    }

    public synchronized void ajustarFaturamento(BigDecimal diferenca, int diferencaDePedidods){
        faturamentoTotal = faturamentoTotal.add(diferenca);
        pedidosFaturados += diferencaDePedidods;
    }

    public synchronized FaturamentoView faturamento(){
        return new FaturamentoView(faturamentoTotal, pedidosFaturados);
    }

    private static String chave(String clienteId){
        return clienteId == null ? "" : clienteId.trim().toLowerCase();
    }
}
