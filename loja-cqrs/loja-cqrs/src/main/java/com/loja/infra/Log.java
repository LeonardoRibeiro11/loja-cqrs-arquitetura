package com.loja.infra;

/** Registro simples no console. Serve para mostrar, na apresentação, por onde cada requisição passa. */
public final class Log {

    private Log() {
    }

    public static synchronized void step(String componente, String mensagem) {
        System.out.println("   [" + String.format("%-15s", componente) + "] " + mensagem);
    }

    public static synchronized void titulo(String texto) {
        System.out.println();
        System.out.println("== " + texto + " ==");
    }

    public static synchronized void linha(String texto) {
        System.out.println(texto);
    }
}
