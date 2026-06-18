package br.com.sebodigital.api.dto.pedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long livroId,
        Long livroCopiaId,
        String titulo,
        String autor,
        String imagemUrl,
        String vendedor,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal) {
}
