package com.loja.infra;

/** Executa um tipo específico de Command. Existe exatamente um handler por comando. */
public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
