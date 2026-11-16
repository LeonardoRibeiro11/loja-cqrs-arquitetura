package com.loja.loja_cqrs.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ItemPedido(String produto, int quantidade, BigDecimal precoUnitario) {
    public ItemPedido{
        if (produto == null || produto.isBlank()){
            throw new RegraDeNegocioException("produto é obrigatório");
        }
        if (quantidade <= 0){
            throw new RegraDeNegocioException("quantidade deve ser maior que zero");
        }
        if (precoUnitario == null || precoUnitario.signum() <= 0){
            throw new RegraDeNegocioException("preço deve ser maior que zero");
        }
        precoUnitario = precoUnitario.setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal subTotal(){
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
