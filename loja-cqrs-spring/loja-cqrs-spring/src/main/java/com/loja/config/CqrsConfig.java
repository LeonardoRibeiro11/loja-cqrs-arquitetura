package com.loja.config;

import com.loja.command.AtualizarStatusPedidoCommand;
import com.loja.command.AtualizarStatusPedidoHandler;
import com.loja.command.CriarPedidoCommand;
import com.loja.command.CriarPedidoHandler;
import com.loja.event.PedidoCriadoEvent;
import com.loja.event.StatusPedidoAtualizadoEvent;
import com.loja.infra.EventBus;
import com.loja.infra.Mediator;
import com.loja.query.ConsultarFaturamentoHandler;
import com.loja.query.ConsultarFaturamentoQuery;
import com.loja.query.ConsultarPedidoHandler;
import com.loja.query.ConsultarPedidoQuery;
import com.loja.query.ListarPedidosPorClienteHandler;
import com.loja.query.ListarPedidosPorClienteQuery;
import com.loja.query.PedidoProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CqrsConfig {

    @Bean(destroyMethod = "encerrar")
    public EventBus eventBus(PedidoProjection projecao,
                             @Value("${loja.broker.latencia-ms:400}") long latenciaMs) {
        EventBus eventBus = new EventBus(latenciaMs);
        eventBus.subscribe(PedidoCriadoEvent.class, projecao::aoCriarPedido);
        eventBus.subscribe(StatusPedidoAtualizadoEvent.class, projecao::aoAtualizarStatus);
        return eventBus;
    }

    @Bean
    public Mediator mediator(CriarPedidoHandler criarPedido,
                             AtualizarStatusPedidoHandler atualizarStatus,
                             ConsultarPedidoHandler consultarPedido,
                             ListarPedidosPorClienteHandler listarPorCliente,
                             ConsultarFaturamentoHandler consultarFaturamento) {
        Mediator mediator = new Mediator();
        mediator.registerCommand(CriarPedidoCommand.class, criarPedido);
        mediator.registerCommand(AtualizarStatusPedidoCommand.class, atualizarStatus);
        mediator.registerQuery(ConsultarPedidoQuery.class, consultarPedido);
        mediator.registerQuery(ListarPedidosPorClienteQuery.class, listarPorCliente);
        mediator.registerQuery(ConsultarFaturamentoQuery.class, consultarFaturamento);
        return mediator;
    }
}
