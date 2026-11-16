package com.loja.loja_cqrs.command;

import com.loja.loja_cqrs.domain.ItemPedido;
import com.loja.loja_cqrs.domain.Pedido;
import com.loja.loja_cqrs.domain.RegraDeNegocioException;
import com.loja.loja_cqrs.event.PedidoCriadoEvent;
import com.loja.loja_cqrs.event.StatusPedidoAtualizadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PedidoCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(PedidoCommandHandler.class);

    private final PedidoWriteRepository bancoDeEscrita;
    private final ApplicationEventPublisher eventos;

    public PedidoCommandHandler(PedidoWriteRepository bancoDeEscrita, ApplicationEventPublisher eventos){
        this.bancoDeEscrita = bancoDeEscrita;
        this.eventos = eventos;
    }

    public long handle(CriarPedidoCommand comando){
        log.info("[COMMAND] criarPedidoCommand recebido (cliente={})", comando.clienteId());

        List<ItemPedido> itens = comando.itens() == null
        ? List.of() : comando.itens().stream()
                .map(i -> new ItemPedido(i.produto(), i.quantidade(), i.precoUnitario()))
                .toList();

        Pedido pedido = Pedido.criar(bancoDeEscrita.proximoId(), comando.clienteId(), itens);
        log.info("[DOMAIN] regras validadas para o pedido #{}", pedido.getId());

        bancoDeEscrita.salvar(pedido);
        log.info("[WRITE DB] pedido #{} salvo (status {})", pedido.getId(), pedido.getStatus());

        eventos.publishEvent(new PedidoCriadoEvent(pedido.getId(), pedido.getClienteId(),
                pedido.getItens().size(), pedido.total(), pedido.getStatus().name()));
        log.info("[EVENT] PedidoCriadoEvent publicado (a leitura sera atualizada em segundo plano");

        return pedido.getId();
    }

    public synchronized void handle(AtualizarStatusPedidoCommand comando){
        log.info("[COMMAND] AtualizarStatusPedidoCommand recebido (pedido=#{}, novo status={})",
                comando.pedidoId(), comando.novoStatus());

        Pedido pedido = bancoDeEscrita.buscarPorId(comando.pedidoId())
                .orElseThrow(() -> new RegraDeNegocioException("pedido #" + comando.pedidoId() + "não encontrado"));

        String statusAnterior = pedido.getStatus().name();
        pedido.atualizarStatus(comando.novoStatus());
        log.info("[DOMAIN] transição {} -> {} aceita", statusAnterior, pedido.getStatus());

        bancoDeEscrita.salvar(pedido);
        log.info("[WRITE DB] pedido #{} atualizado", pedido.getId());

        eventos.publishEvent(new StatusPedidoAtualizadoEvent(pedido.getId(), statusAnterior,
                pedido.getStatus().name(), pedido.total()));
        log.info("[EVENT] StatusPedidoAtualizadoEvent publicado");
    }
}
