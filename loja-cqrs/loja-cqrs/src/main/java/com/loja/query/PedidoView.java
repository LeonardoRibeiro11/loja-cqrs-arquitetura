package com.loja.query;

import com.loja.infra.Formato;

import java.math.BigDecimal;

/**
 * READ MODEL / DTO: visão desnormalizada do pedido, pronta para exibição.
 * Não tem regras de negócio e não depende das classes do domínio.
 */
public record PedidoView(long id, String clienteId, int quantidadeItens,
                         BigDecimal total, String status, int versao) {

    @Override
    public String toString() {
        return "Pedido #" + id + " | cliente=" + clienteId + " | itens=" + quantidadeItens
                + " | total=" + Formato.moeda(total) + " | status=" + status;
    }
}
