package com.loja.query;

import com.loja.infra.Log;
import com.loja.infra.QueryHandler;

/** QUERY HANDLER: devolve o faturamento que a projeção já deixou calculado. */
public class ConsultarFaturamentoHandler implements QueryHandler<ConsultarFaturamentoQuery, FaturamentoView> {

    private final PedidoReadStore readDb;

    public ConsultarFaturamentoHandler(PedidoReadStore readDb) {
        this.readDb = readDb;
    }

    @Override
    public FaturamentoView handle(ConsultarFaturamentoQuery consulta) {
        Log.step("READ DB", "leitura do faturamento já acumulado");
        return readDb.faturamento();
    }
}
