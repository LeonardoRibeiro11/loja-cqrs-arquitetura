package com.loja.loja_cqrs.domain;

public class RegraDeNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1l;

    public RegraDeNegocioException(String mensagem){
        super(mensagem);
    }
}
