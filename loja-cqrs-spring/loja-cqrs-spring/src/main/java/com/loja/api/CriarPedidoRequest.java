package com.loja.api;

import java.math.BigDecimal;
import java.util.List;

public record CriarPedidoRequest(String clienteId, List<Item> itens) {

    public record Item(String produto, int quantidade, BigDecimal precoUnitario) {
    }
}
