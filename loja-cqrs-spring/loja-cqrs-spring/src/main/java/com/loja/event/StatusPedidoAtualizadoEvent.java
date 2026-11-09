package com.loja.event;

import com.loja.infra.DomainEvent;

import java.math.BigDecimal;

public record StatusPedidoAtualizadoEvent(long pedidoId, int versao, String statusAnterior,
                                          String statusNovo, BigDecimal total) implements DomainEvent {
}
