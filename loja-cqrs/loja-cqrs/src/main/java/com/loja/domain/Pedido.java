package com.loja.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * DOMAIN MODEL (lado de escrita). Concentra as regras de negócio e as invariantes do pedido.
 * Só o lado de escrita conhece esta classe: o lado de leitura trabalha com PedidoView.
 */
public class Pedido {

    private final long id;
    private final String clienteId;
    private final List<ItemPedido> itens;
    private StatusPedido status;
    private int versao;

    private Pedido(long id, String clienteId, List<ItemPedido> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.itens = List.copyOf(itens);
        this.status = StatusPedido.CRIADO;
        this.versao = 1;
    }

    public static Pedido criar(long id, String clienteId, List<ItemPedido> itens) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new RegraDeNegocioException("Cliente é obrigatório.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new RegraDeNegocioException("O pedido precisa ter ao menos um item.");
        }
        return new Pedido(id, clienteId, itens);
    }

    public void atualizarStatus(StatusPedido novo) {
        if (!status.podeIrPara(novo)) {
            throw new RegraDeNegocioException("Transição inválida: " + status + " -> " + novo);
        }
        this.status = novo;
        this.versao++;
    }

    public BigDecimal total() {
        return itens.stream().map(ItemPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public int getVersao() {
        return versao;
    }
}
