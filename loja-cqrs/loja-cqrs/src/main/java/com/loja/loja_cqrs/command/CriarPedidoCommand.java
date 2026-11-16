package com.loja.loja_cqrs.command;

import java.math.BigDecimal;
import java.util.List;

public record CriarPedidoCommand(String clienteId, List<Item> itens) {
    public record Item(String produto, int quantidade, BigDecimal precoUnitario){
    }
}
