package com.loja.loja_cqrs.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class PedidoQueryHandler {
    private static final Logger log = LoggerFactory.getLogger(PedidoQueryHandler.class);
    private final PedidoReadRepository bancoDeLeitura;

    public PedidoQueryHandler(PedidoReadRepository bancoDeLeitura){
        this.bancoDeLeitura = bancoDeLeitura;
    }

    public Optional<PedidoView> handle(ConsultarPedidoQuery consulta){
        log.info("[QUERY] ConsultarPedidoQuery -> [READ DB] consulta direta do pediddo #{}", consulta.pedidoId());
        return bancoDeLeitura.buscarPorId(consulta.pedidoId());
    }

    public List<PedidoView> handle(ListarPedidosPorClienteQuery consulta){
        log.info("[QUERY] ListarPedidosPorClienteQuery -> [READ DB] consulta direta (cliente={})", consulta.clienteId());
        return bancoDeLeitura.listarPorCliente(consulta.clienteId());
    }

    public FaturamentoView handle(ConsultarFaturamentoQuery consulta){
        log.info("[QUERY] ConsultarFaturamentoQuery -> [READ DB] leitura do faturamento ja somado");
        return bancoDeLeitura.faturamento();
    }
}
