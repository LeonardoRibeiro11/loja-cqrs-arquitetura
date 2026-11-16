package com.loja.loja_cqrs.command;

import com.loja.loja_cqrs.domain.StatusPedido;

public record AtualizarStatusPedidoCommand(long pedidoId, StatusPedido novoStatus){
}