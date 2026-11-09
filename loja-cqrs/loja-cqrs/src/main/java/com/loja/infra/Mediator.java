package com.loja.infra;

import java.util.HashMap;
import java.util.Map;

/**
 * CONECTOR em memória (Command Bus + Query Bus).
 * Recebe um Command ou uma Query e a entrega ao handler registrado para aquele tipo,
 * sem que o controller conheça a implementação do handler.
 */
public class Mediator {

    private final Map<Class<?>, CommandHandler<?, ?>> commandHandlers = new HashMap<>();
    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers = new HashMap<>();

    public <C extends Command<R>, R> void registerCommand(Class<C> tipo, CommandHandler<C, R> handler) {
        commandHandlers.put(tipo, handler);
    }

    public <Q extends Query<R>, R> void registerQuery(Class<Q> tipo, QueryHandler<Q, R> handler) {
        queryHandlers.put(tipo, handler);
    }

    /** Envia um comando (escrita). Resposta síncrona: aceite ou erro. */
    @SuppressWarnings("unchecked")
    public <R> R send(Command<R> command) {
        CommandHandler<Command<R>, R> handler =
                (CommandHandler<Command<R>, R>) commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalStateException("Nenhum handler registrado para " + command.getClass().getSimpleName());
        }
        Log.step("COMMAND BUS", command.getClass().getSimpleName() + " -> " + handler.getClass().getSimpleName());
        return handler.handle(command);
    }

    /** Envia uma consulta (leitura). Resposta síncrona com o resultado. */
    @SuppressWarnings("unchecked")
    public <R> R ask(Query<R> query) {
        QueryHandler<Query<R>, R> handler =
                (QueryHandler<Query<R>, R>) queryHandlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalStateException("Nenhum handler registrado para " + query.getClass().getSimpleName());
        }
        Log.step("QUERY BUS", query.getClass().getSimpleName() + " -> " + handler.getClass().getSimpleName());
        return handler.handle(query);
    }
}
