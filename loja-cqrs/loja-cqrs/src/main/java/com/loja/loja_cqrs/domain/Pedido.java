package com.loja.loja_cqrs.domain;

import java.math.BigDecimal;
import java.util.List;

public class Pedido {
    private final long id;
    private final String clienteId;
    private final List<ItemPedido> itens;
    private StatusPedido status;

    private Pedido(long id, String clienteId, List<ItemPedido> itens){
        this.id = id;
        this.clienteId = clienteId;
        this.itens = List.copyOf(itens);
        this.status = StatusPedido.CRIADO;
    }

    public static Pedido criar(long id, String clienteId, List<ItemPedido> itens){
        if (clienteId == null || clienteId.isBlank()){
            throw new RegraDeNegocioException("cliente é obrigatório");
        }
        if (itens == null || itens.isEmpty()){
            throw new RegraDeNegocioException("o pedido precisa ter pelo menos um item");
        }
        return new Pedido(id, clienteId.trim(), itens);
    }

    public void atualizarStatus(StatusPedido novoStatus){
        if (!status.podeIrPara(novoStatus)){
            throw new RegraDeNegocioException("transição inválida: " + status + " -> " + novoStatus);
        }
        this.status = novoStatus;
    }

    public BigDecimal total(){
        return itens.stream().map(ItemPedido::subTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getId(){
        return id;
    }
    public String getClienteId(){
        return clienteId;
    }
    public List<ItemPedido> getItens(){
        return itens;
    }
    public StatusPedido getStatus(){
        return status;
    }
}
