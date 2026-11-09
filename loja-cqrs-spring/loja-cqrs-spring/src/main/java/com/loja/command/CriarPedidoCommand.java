package com.loja.command;

import com.loja.domain.ItemPedido;
import com.loja.infra.Command;

import java.util.List;

public record CriarPedidoCommand(String clienteId, List<ItemPedido> itens) implements Command<Long> {
}
