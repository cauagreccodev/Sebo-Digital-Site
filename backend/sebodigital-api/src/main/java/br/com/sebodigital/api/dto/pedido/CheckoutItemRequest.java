package br.com.sebodigital.api.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CheckoutItemRequest(
        @NotNull(message = "A oferta do livro e obrigatoria")
        Long livroCopiaId,

        @NotNull(message = "A quantidade e obrigatoria")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade) {
}
