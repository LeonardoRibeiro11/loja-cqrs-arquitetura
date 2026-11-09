package com.loja.api;

import com.loja.command.AtualizarStatusPedidoCommand;
import com.loja.command.CriarPedidoCommand;
import com.loja.domain.ItemPedido;
import com.loja.domain.RegraDeNegocioException;
import com.loja.infra.Mediator;
import com.loja.query.ConsultarFaturamentoQuery;
import com.loja.query.ConsultarPedidoQuery;
import com.loja.query.FaturamentoView;
import com.loja.query.ListarPedidosPorClienteQuery;
import com.loja.query.PedidoView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
public class PedidoController {

    private final Mediator mediator;

    public PedidoController(Mediator mediator) {
        this.mediator = mediator;
    }

    // ---------------- ESCRITA (Commands) ----------------

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Long>> criarPedido(@RequestBody CriarPedidoRequest requisicao) {
        List<ItemPedido> itens = requisicao.itens() == null
                ? List.of()
                : requisicao.itens().stream()
                        .map(i -> new ItemPedido(i.produto(), i.quantidade(), i.precoUnitario()))
                        .toList();
        long id = mediator.send(new CriarPedidoCommand(normalizar(requisicao.clienteId()), itens));
        return ResponseEntity.created(URI.create("/pedidos/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/pedidos/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable("id") long id,
                                                @RequestBody AtualizarStatusRequest requisicao) {
        if (requisicao.status() == null) {
            throw new RegraDeNegocioException("O novo status é obrigatório.");
        }
        mediator.send(new AtualizarStatusPedidoCommand(id, requisicao.status()));
        return ResponseEntity.accepted().build();
    }

    // ---------------- LEITURA (Queries) ----------------

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoView> consultarPedido(@PathVariable("id") long id) {
        return mediator.ask(new ConsultarPedidoQuery(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedidos")
    public List<PedidoView> listarPorCliente(@RequestParam("clienteId") String clienteId) {
        return mediator.ask(new ListarPedidosPorClienteQuery(normalizar(clienteId)));
    }

    @GetMapping("/faturamento")
    public FaturamentoView consultarFaturamento() {
        return mediator.ask(new ConsultarFaturamentoQuery());
    }

    private static String normalizar(String clienteId) {
        return clienteId == null ? null : clienteId.trim().toLowerCase();
    }
}
