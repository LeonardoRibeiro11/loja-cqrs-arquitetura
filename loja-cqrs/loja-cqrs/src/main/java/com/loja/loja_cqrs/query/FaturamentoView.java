package com.loja.loja_cqrs.query;

import java.math.BigDecimal;

public record FaturamentoView(BigDecimal total, int pedidosFaturados) {
}
