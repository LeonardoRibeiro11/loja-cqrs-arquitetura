package com.loja.api;

import com.loja.command.AtualizarStatusPedidoCommand;
import com.loja.command.CriarPedidoCommand;
import com.loja.domain.ItemPedido;
import com.loja.domain.StatusPedido;
import com.loja.infra.Mediator;
import com.loja.query.ConsultarFaturamentoQuery;
import com.loja.query.ConsultarPedidoQuery;
import com.loja.query.FaturamentoView;
import com.loja.query.ListarPedidosPorClienteQuery;
import com.loja.query.PedidoView;

import java.util.List;
import java.util.Optional;

/**
 * ENTRADA (API). Traduz cada chamada do usuário em um Command ou em uma Query e entrega ao Mediator.
 * Não tem regra de negócio e não conhece handlers, bancos nem eventos.
 * Em um sistema real seria um controller REST (ex.: Spring @RestController).
 */
public class PedidoController {

    private final Mediator mediator;

    public PedidoController(Mediator mediator) {
        this.mediator = mediator;
    }

    // ---- ESCRITA (Commands) ----

    public long criarPedido(String clienteId, List<ItemPedido> itens) {
        return mediator.send(new CriarPedidoCommand(normalizar(clienteId), itens));
    }

    public void atualizarStatus(long pedidoId, StatusPedido novoStatus) {
        mediator.send(new AtualizarStatusPedidoCommand(pedidoId, novoStatus));
    }

    // ---- LEITURA (Queries) ----

    public Optional<PedidoView> consultarPedido(long pedidoId) {
        return mediator.ask(new ConsultarPedidoQuery(pedidoId));
    }

    public List<PedidoView> listarPorCliente(String clienteId) {
        return mediator.ask(new ListarPedidosPorClienteQuery(normalizar(clienteId)));
    }

    public FaturamentoView consultarFaturamento() {
        return mediator.ask(new ConsultarFaturamentoQuery());
    }

    private static String normalizar(String clienteId) {
        return clienteId == null ? null : clienteId.trim().toLowerCase();
    }
}
