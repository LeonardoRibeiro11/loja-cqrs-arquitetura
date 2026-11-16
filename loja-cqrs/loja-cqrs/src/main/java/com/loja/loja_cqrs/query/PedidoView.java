package com.loja.loja_cqrs.query;

import java.math.BigDecimal;

public record PedidoView(long id, String clienteId, int quantidadeItens, BigDecimal total, String status) {
}
