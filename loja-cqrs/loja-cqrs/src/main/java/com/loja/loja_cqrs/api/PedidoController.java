package com.loja.loja_cqrs.api;

import com.loja.loja_cqrs.command.AtualizarStatusPedidoCommand;
import com.loja.loja_cqrs.command.CriarPedidoCommand;
import com.loja.loja_cqrs.command.PedidoCommandHandler;
import com.loja.loja_cqrs.domain.StatusPedido;
import com.loja.loja_cqrs.query.*;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
public class PedidoController {
    private final PedidoCommandHandler comandos;
    private final PedidoQueryHandler consultas;

    public PedidoController(PedidoCommandHandler comandos, PedidoQueryHandler consultas){
        this.comandos = comandos;
        this.consultas = consultas;
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Long>> criarPedido(@RequestBody CriarPedidoCommand comando){
        long id = comandos.handle(comando);
        return ResponseEntity.created(URI.create("/pedidos/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/pedidos/{id}/status/{novoStatus}")
    public ResponseEntity<Void> atualizarStatus(@PathVariable("id") long id, @PathVariable("novoStatus")StatusPedido novoStatus){
        comandos.handle(new AtualizarStatusPedidoCommand(id, novoStatus));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoView> consultarPedido(@PathVariable("id") long id){
        return consultas.handle(new ConsultarPedidoQuery(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedidos")
    public List<PedidoView> listarPorCliente(@RequestParam("clienteId") String clienteId){
        return consultas.handle(new ListarPedidosPorClienteQuery(clienteId));
    }

    @GetMapping("/faturamento")
    public FaturamentoView consultarFaturamento(){
        return consultas.handle(new ConsultarFaturamentoQuery());
    }

}
