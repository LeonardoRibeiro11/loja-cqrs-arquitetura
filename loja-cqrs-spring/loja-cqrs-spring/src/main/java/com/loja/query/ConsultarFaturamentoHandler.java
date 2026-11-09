package com.loja.query;

import org.springframework.stereotype.Component;
import com.loja.infra.Log;
import com.loja.infra.QueryHandler;

@Component
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
