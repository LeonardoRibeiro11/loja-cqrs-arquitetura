package com.loja.command;

import com.loja.domain.ItemPedido;
import com.loja.infra.Command;

import java.util.List;

/** Intenção: cadastrar um pedido. Devolve apenas o id gerado. */
public record CriarPedidoCommand(String clienteId, List<ItemPedido> itens) implements Command<Long> {
}
