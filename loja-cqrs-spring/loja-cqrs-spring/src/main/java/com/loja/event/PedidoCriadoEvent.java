package com.loja.event;

import com.loja.infra.DomainEvent;

import java.math.BigDecimal;

public record PedidoCriadoEvent(long pedidoId, int versao, String clienteId,
                                int quantidadeItens, BigDecimal total, String status) implements DomainEvent {
}
