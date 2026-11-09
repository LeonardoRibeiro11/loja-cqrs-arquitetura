package com.loja.api;

import com.loja.domain.StatusPedido;

public record AtualizarStatusRequest(StatusPedido status) {
}
