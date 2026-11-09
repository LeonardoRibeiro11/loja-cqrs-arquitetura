package com.loja.infra;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
