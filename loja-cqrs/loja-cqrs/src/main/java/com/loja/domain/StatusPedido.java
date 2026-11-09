package com.loja.domain;

/** Ciclo de vida do pedido. As transições permitidas são uma regra de negócio do domínio. */
public enum StatusPedido {
    CRIADO, PAGO, ENVIADO, ENTREGUE, CANCELADO;

    public boolean podeIrPara(StatusPedido destino) {
        return switch (this) {
            case CRIADO -> destino == PAGO || destino == CANCELADO;
            case PAGO -> destino == ENVIADO || destino == CANCELADO;
            case ENVIADO -> destino == ENTREGUE;
            case ENTREGUE, CANCELADO -> false;
        };
    }
}
