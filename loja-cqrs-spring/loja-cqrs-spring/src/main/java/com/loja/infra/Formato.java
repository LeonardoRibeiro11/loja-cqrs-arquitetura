package com.loja.infra;

import java.math.BigDecimal;
import java.util.Locale;

public final class Formato {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Formato() {
    }

    public static String moeda(BigDecimal valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }
}
