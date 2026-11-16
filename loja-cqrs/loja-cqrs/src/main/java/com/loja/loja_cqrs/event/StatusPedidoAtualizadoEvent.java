package com.loja.loja_cqrs.event;

import java.math.BigDecimal;

public record StatusPedidoAtualizadoEvent (long pedidoId, String statusAnterior, String statusNovo, BigDecimal total){
}
