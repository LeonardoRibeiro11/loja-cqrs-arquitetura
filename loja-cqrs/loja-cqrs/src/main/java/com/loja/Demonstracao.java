package com.loja;

import com.loja.api.PedidoController;
import com.loja.domain.ItemPedido;
import com.loja.domain.RegraDeNegocioException;
import com.loja.domain.StatusPedido;
import com.loja.infra.EventBus;
import com.loja.infra.Log;
import com.loja.query.PedidoView;

import java.util.List;

/** Demonstração roteirizada. Cada bloco mostra um comportamento do padrão. */
public class Demonstracao {

    private final PedidoController controller;
    private final EventBus eventBus;

    public Demonstracao(PedidoController controller, EventBus eventBus) {
        this.controller = controller;
        this.eventBus = eventBus;
    }

    public void executar() {
        Log.titulo("1. Cadastrar pedidos (lado de ESCRITA)");
        long p1 = controller.criarPedido("Ana", List.of(
                ItemPedido.de("Teclado", 1, "250.00"), ItemPedido.de("Mouse", 2, "80.00")));
        long p2 = controller.criarPedido("Bruno", List.of(ItemPedido.de("Monitor", 1, "900.00")));
        long p3 = controller.criarPedido("Ana", List.of(ItemPedido.de("Cabo HDMI", 3, "25.00")));

        Log.titulo("2. Consistência eventual: consulta feita ANTES de o evento chegar ao Read DB");
        Log.linha("   Resultado: " + controller.consultarPedido(p1)
                .map(PedidoView::toString).orElse("(pedido ainda não existe no banco de leitura)"));

        Log.titulo("3. Aguardando a sincronização (o evento chega ao Read DB)");
        eventBus.aguardarEntrega();
        Log.titulo("4. Mesma consulta, agora depois da sincronização (lado de LEITURA)");
        Log.linha("   Resultado: " + controller.consultarPedido(p1).map(PedidoView::toString).orElse("(não encontrado)"));

        Log.titulo("5. Atualizar status (lado de ESCRITA)");
        controller.atualizarStatus(p1, StatusPedido.PAGO);
        controller.atualizarStatus(p2, StatusPedido.PAGO);
        controller.atualizarStatus(p2, StatusPedido.ENVIADO);
        eventBus.aguardarEntrega();

        Log.titulo("6. Listar pedidos por cliente e consultar faturamento (lado de LEITURA)");
        List<PedidoView> daAna = controller.listarPorCliente("Ana");
        Log.linha("   Pedidos da Ana:");
        daAna.forEach(v -> Log.linha("     " + v));
        Log.linha("   Resultado: " + controller.consultarFaturamento());

        Log.titulo("7. O Domain Model protege as regras de negócio");
        Log.linha("   Tentando mudar o pedido #" + p3 + " de CRIADO direto para ENTREGUE...");
        try {
            controller.atualizarStatus(p3, StatusPedido.ENTREGUE);
        } catch (RegraDeNegocioException e) {
            Log.linha("   Rejeitado pelo domínio: " + e.getMessage() + " (nenhum evento foi publicado)");
        }

        Log.titulo("8. Cancelar um pedido pago ajusta o faturamento");
        controller.atualizarStatus(p1, StatusPedido.CANCELADO);
        eventBus.aguardarEntrega();
        Log.linha("   Resultado: " + controller.consultarFaturamento());
        Log.linha("   Resultado: " + controller.consultarPedido(p1).map(PedidoView::toString).orElse("(não encontrado)"));
    }
}
