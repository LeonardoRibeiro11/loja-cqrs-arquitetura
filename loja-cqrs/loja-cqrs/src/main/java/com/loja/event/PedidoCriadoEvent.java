package com.loja.event;

import com.loja.infra.DomainEvent;

import java.math.BigDecimal;

/**
 * Evento emitido pelo lado de escrita quando um pedido é criado.
 * Carrega tudo o que o lado de leitura precisa (inclusive o total), para não consultar o Write DB.
 * O status vai como texto para que o lado de leitura não dependa das classes do domínio.
 */
public record PedidoCriadoEvent(long pedidoId, int versao, String clienteId,
                                int quantidadeItens, BigDecimal total, String status) implements DomainEvent {
}
