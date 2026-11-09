package com.loja.command;

import com.loja.domain.StatusPedido;
import com.loja.infra.Command;

/** Intenção: mudar o status de um pedido. Não devolve dados. */
public record AtualizarStatusPedidoCommand(long pedidoId, StatusPedido novoStatus) implements Command<Void> {
}
