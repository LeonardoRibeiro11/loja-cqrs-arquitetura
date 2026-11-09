package com.loja.infra;

public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
