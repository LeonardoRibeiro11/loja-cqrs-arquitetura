package com.loja.event;

import com.loja.infra.DomainEvent;

import java.math.BigDecimal;

/** Evento emitido quando o status de um pedido muda. A versão permite ao consumidor ignorar duplicatas. */
public record StatusPedidoAtualizadoEvent(long pedidoId, int versao, String statusAnterior,
                                          String statusNovo, BigDecimal total) implements DomainEvent {
}
