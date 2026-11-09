package com.loja.domain;

/** Violação de uma regra de negócio (ex.: transição de status inválida). */
public class RegraDeNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
