package br.com.sebodigital.api.dto.pedido;

import br.com.sebodigital.api.model.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record StatusPedidoRequest(
        @NotNull(message = "Informe o novo status")
        StatusPedido status) {
}
