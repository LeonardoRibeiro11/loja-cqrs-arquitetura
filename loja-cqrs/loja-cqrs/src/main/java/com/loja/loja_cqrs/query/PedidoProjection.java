package com.loja.loja_cqrs.query;

import com.loja.loja_cqrs.domain.Pedido;
import com.loja.loja_cqrs.event.PedidoCriadoEvent;
import com.loja.loja_cqrs.event.StatusPedidoAtualizadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class PedidoProjection {
    private static final Logger log = LoggerFactory.getLogger(PedidoProjection.class);

    private static final Set<String> FATURAVEIS = Set.of("PAGO", "ENVIADO", "ENTRGUE");

    private final PedidoReadRepository bancoDeLeitura;
    private final long atrasoMs;

    public PedidoProjection(PedidoReadRepository bancoDeLeitura, @Value("${loja.sincronizacao.atraso-ms:400}") long atrasoMs){
        this.bancoDeLeitura = bancoDeLeitura;
        this.atrasoMs = atrasoMs;
    }

    @Async("sincronizacaoExecutor")
    @EventListener
    public void aoCriarPedido(PedidoCriadoEvent evento){
        esperarUmPouco();
        log.info("[PROJECTION] recebeu PedidoCriadoEvent do pedido #{}", evento.pedidoId());

        bancoDeLeitura.salvar(new PedidoView(evento.pedidoId(), evento.clienteId(), evento.quantidadeItens(),
                evento.total(), evento.status()));
        if (FATURAVEIS.contains(evento.status())){
            bancoDeLeitura.ajustarFaturamento(evento.total(), 1);
        }
        log.info("[READ DB] read model do pedido #{} gravado", evento.pedidoId());
    }

    @Async("sincronizacaoExecutor")
    @EventListener
    public void aoAtualizarStatus(StatusPedidoAtualizadoEvent evento){
        esperarUmPouco();
        log.info("[PROJECTION] recebeu StatusPedidoAtualizadoEvent do pedido #{} ({} -> {})",
                evento.pedidoId(), evento.statusAnterior(), evento.statusNovo());

        Optional<PedidoView> atual = bancoDeLeitura.buscarPorId(evento.pedidoId());
        if (atual.isEmpty()){
            log.warn("[PROJECTION] pedido #{} não existe no banco de leitura; evento ignorado", evento.pedidoId());
            return;
        }
        PedidoView pedido = atual.get();
        bancoDeLeitura.salvar(new PedidoView(pedido.id(), pedido.clienteId(), pedido.quantidadeItens(),
                pedido.total(), evento.statusNovo()));

        boolean contavaAntes = FATURAVEIS.contains(evento.statusAnterior());
        boolean contaAgora = FATURAVEIS.contains(evento.statusNovo());

        if (!contavaAntes && contaAgora){
            bancoDeLeitura.ajustarFaturamento(evento.total(), 1);
        } else if (contavaAntes && !contaAgora){
            bancoDeLeitura.ajustarFaturamento(evento.total().negate(), -1);
        }
        log.info("[READ DB] read model do pedido #{} atualizado", evento.pedidoId());
    }
    private void esperarUmPouco(){
        try{
            Thread.sleep(atrasoMs);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
