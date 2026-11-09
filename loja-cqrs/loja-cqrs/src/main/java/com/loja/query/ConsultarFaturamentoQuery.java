package com.loja.query;

import com.loja.infra.Query;

/** Consulta: faturamento total. */
public record ConsultarFaturamentoQuery() implements Query<FaturamentoView> {
}
