package com.loja.loja_cqrs.event;

import java.math.BigDecimal;

public record PedidoCriadoEvent (long pedidoId, String clienteId, int quantidadeItens, BigDecimal total, String status){
}
