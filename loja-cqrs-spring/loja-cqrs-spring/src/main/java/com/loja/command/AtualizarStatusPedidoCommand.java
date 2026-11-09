package com.loja.command;

import com.loja.domain.StatusPedido;
import com.loja.infra.Command;

public record AtualizarStatusPedidoCommand(long pedidoId, StatusPedido novoStatus) implements Command<Void> {
}
