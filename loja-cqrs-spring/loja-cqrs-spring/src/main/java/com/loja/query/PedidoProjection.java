package com.loja.query;

import org.springframework.stereotype.Component;
import com.loja.event.PedidoCriadoEvent;
import com.loja.event.StatusPedidoAtualizadoEvent;
import com.loja.infra.Log;

import java.util.Optional;
import java.util.Set;

@Component
public class PedidoProjection {


    private static final Set<String> FATURAVEIS = Set.of("PAGO", "ENVIADO", "ENTREGUE");

    private final PedidoReadStore readDb;

    public PedidoProjection(PedidoReadStore readDb) {
        this.readDb = readDb;
    }

    public void aoCriarPedido(PedidoCriadoEvent evento) {
        Log.step("PROJECTION", "recebeu PedidoCriadoEvent do pedido #" + evento.pedidoId());
        if (readDb.buscar(evento.pedidoId()).isPresent()) {
            Log.step("PROJECTION", "evento duplicado ignorado (idempotência)");
            return;
        }
        readDb.salvar(new PedidoView(evento.pedidoId(), evento.clienteId(), evento.quantidadeItens(),
                evento.total(), evento.status(), evento.versao()));
        if (FATURAVEIS.contains(evento.status())) {
            readDb.ajustarFaturamento(evento.total(), 1);
        }
        Log.step("READ DB", "Read Model do pedido #" + evento.pedidoId() + " gravado");
    }

    public void aoAtualizarStatus(StatusPedidoAtualizadoEvent evento) {
        Log.step("PROJECTION", "recebeu StatusPedidoAtualizadoEvent do pedido #" + evento.pedidoId()
                + " (" + evento.statusAnterior() + " -> " + evento.statusNovo() + ")");
        Optional<PedidoView> atual = readDb.buscar(evento.pedidoId());
        if (atual.isEmpty()) {
            Log.step("PROJECTION", "pedido ainda não existe no Read DB, evento ignorado");
            return;
        }
        PedidoView view = atual.get();
        if (evento.versao() <= view.versao()) {
            Log.step("PROJECTION", "evento duplicado ou antigo ignorado (idempotência)");
            return;
        }
        readDb.salvar(new PedidoView(view.id(), view.clienteId(), view.quantidadeItens(),
                view.total(), evento.statusNovo(), evento.versao()));

        boolean contavaAntes = FATURAVEIS.contains(evento.statusAnterior());
        boolean contaAgora = FATURAVEIS.contains(evento.statusNovo());
        if (!contavaAntes && contaAgora) {
            readDb.ajustarFaturamento(evento.total(), 1);
        } else if (contavaAntes && !contaAgora) {
            readDb.ajustarFaturamento(evento.total().negate(), -1);
        }
        Log.step("READ DB", "Read Model do pedido #" + evento.pedidoId() + " atualizado");
    }
}
