package com.loja.domain;

public class PedidoNaoEncontradoException extends RegraDeNegocioException {

    private static final long serialVersionUID = 1L;

    public PedidoNaoEncontradoException(long pedidoId) {
        super("Pedido #" + pedidoId + " não encontrado.");
    }
}
