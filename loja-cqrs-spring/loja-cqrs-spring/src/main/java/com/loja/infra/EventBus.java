package com.loja.infra;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventBus {

    private final Map<Class<? extends DomainEvent>, List<Consumer<DomainEvent>>> assinantes = new ConcurrentHashMap<>();
    private final ExecutorService entregador = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "event-bus");
        t.setDaemon(true);
        return t;
    });
    private final long latenciaMs;

    public EventBus(long latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public <E extends DomainEvent> void subscribe(Class<E> tipo, Consumer<E> consumidor) {
        assinantes.computeIfAbsent(tipo, k -> new CopyOnWriteArrayList<>())
                .add(evento -> consumidor.accept(tipo.cast(evento)));
    }

    public void publish(DomainEvent evento) {
        Log.step("EVENT BUS", "publicou " + evento.getClass().getSimpleName() + " (entrega assíncrona)");
        entregador.submit(() -> {
            try {
                Thread.sleep(latenciaMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            for (Consumer<DomainEvent> consumidor : assinantes.getOrDefault(evento.getClass(), List.of())) {
                try {
                    consumidor.accept(evento);
                } catch (RuntimeException e) {
                    Log.step("EVENT BUS", "erro no consumidor: " + e.getMessage());
                }
            }
        });
    }

    /** Espera todos os eventos já publicados serem entregues. Usado só na demonstração. */
    public void aguardarEntrega() {
        try {
            entregador.submit(() -> { }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public void encerrar() {
        entregador.shutdown();
    }
}
