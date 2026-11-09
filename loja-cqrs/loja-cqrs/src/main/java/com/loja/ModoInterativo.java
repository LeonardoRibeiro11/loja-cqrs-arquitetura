package com.loja;

import com.loja.api.PedidoController;
import com.loja.domain.ItemPedido;
import com.loja.domain.RegraDeNegocioException;
import com.loja.domain.StatusPedido;
import com.loja.infra.Log;
import com.loja.query.PedidoView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Menu de console para testar o sistema manualmente. */
public class ModoInterativo {

    private final PedidoController controller;
    private final Scanner entrada = new Scanner(System.in);

    public ModoInterativo(PedidoController controller) {
        this.controller = controller;
    }

    public void executar() {
        Log.linha("Sistema de pedidos (CQRS). Depois de um comando, a leitura leva ~400 ms para refletir a mudança.");
        while (true) {
            Log.linha("");
            Log.linha("1) Cadastrar pedido   2) Atualizar status   3) Consultar pedido");
            Log.linha("4) Listar por cliente 5) Consultar faturamento   0) Sair");
            String opcao = ler("Opção: ");
            if (opcao == null || opcao.equals("0")) {
                Log.linha("Até mais!");
                return;
            }
            try {
                switch (opcao) {
                    case "1" -> cadastrar();
                    case "2" -> atualizar();
                    case "3" -> consultar();
                    case "4" -> listar();
                    case "5" -> Log.linha("   Resultado: " + controller.consultarFaturamento());
                    default -> Log.linha("Opção inválida.");
                }
            } catch (RegraDeNegocioException e) {
                Log.linha("   Rejeitado: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.linha("   Entrada inválida: " + e.getMessage());
            }
        }
    }

    private void cadastrar() {
        String cliente = ler("Cliente: ");
        List<ItemPedido> itens = new ArrayList<>();
        do {
            String produto = ler("  Produto: ");
            int quantidade = Integer.parseInt(ler("  Quantidade: ").trim());
            BigDecimal preco = new BigDecimal(ler("  Preço unitário (ex.: 49.90): ").trim().replace(',', '.'));
            itens.add(new ItemPedido(produto, quantidade, preco));
        } while ("s".equalsIgnoreCase(ler("  Adicionar outro item? (s/n): ").trim()));
        long id = controller.criarPedido(cliente, itens);
        Log.linha("   Pedido #" + id + " cadastrado.");
    }

    private void atualizar() {
        long id = Long.parseLong(ler("Id do pedido: ").trim());
        String texto = ler("Novo status (CRIADO, PAGO, ENVIADO, ENTREGUE, CANCELADO): ").trim().toUpperCase();
        StatusPedido status;
        try {
            status = StatusPedido.valueOf(texto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("status desconhecido: " + texto);
        }
        controller.atualizarStatus(id, status);
        Log.linha("   Comando aceito.");
    }

    private void consultar() {
        long id = Long.parseLong(ler("Id do pedido: ").trim());
        Log.linha("   Resultado: " + controller.consultarPedido(id)
                .map(PedidoView::toString).orElse("(não encontrado no banco de leitura)"));
    }

    private void listar() {
        List<PedidoView> lista = controller.listarPorCliente(ler("Cliente: "));
        if (lista.isEmpty()) {
            Log.linha("   (nenhum pedido encontrado)");
        }
        lista.forEach(v -> Log.linha("   " + v));
    }

    private String ler(String pergunta) {
        System.out.print(pergunta);
        System.out.flush();
        return entrada.hasNextLine() ? entrada.nextLine() : null;
    }
}
