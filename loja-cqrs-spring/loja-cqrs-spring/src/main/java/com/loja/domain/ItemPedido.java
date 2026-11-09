package com.loja.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ItemPedido(String produto, int quantidade, BigDecimal precoUnitario) {

    public ItemPedido {
        if (produto == null || produto.isBlank()) {
            throw new RegraDeNegocioException("Produto é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new RegraDeNegocioException("Quantidade deve ser maior que zero.");
        }
        if (precoUnitario == null || precoUnitario.signum() <= 0) {
            throw new RegraDeNegocioException("Preço deve ser maior que zero.");
        }
        precoUnitario = precoUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    public static ItemPedido de(String produto, int quantidade, String preco) {
        return new ItemPedido(produto, quantidade, new BigDecimal(preco));
    }

    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
