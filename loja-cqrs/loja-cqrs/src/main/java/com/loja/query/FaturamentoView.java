package com.loja.query;

import com.loja.infra.Formato;

import java.math.BigDecimal;

/** READ MODEL do faturamento: já calculado, então consultar não exige somar pedido por pedido. */
public record FaturamentoView(BigDecimal total, int pedidosFaturados) {

    @Override
    public String toString() {
        return "Faturamento = " + Formato.moeda(total) + " (" + pedidosFaturados + " pedido(s) pago(s), enviado(s) ou entregue(s))";
    }
}
