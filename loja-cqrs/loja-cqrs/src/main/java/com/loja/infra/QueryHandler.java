package com.loja.infra;

/** Responde a um tipo específico de Query, direto na fonte de leitura. */
public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
