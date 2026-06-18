package br.com.sebodigital.api.dto.pedido;

import br.com.sebodigital.api.model.enums.StatusPedido;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
        Long id,
        String codigo,
        StatusPedido status,
        Instant criadoEm,
        Instant atualizadoEm,
        LocalDate previsaoEntrega,
        String enderecoEntrega,
        String cidadeEntrega,
        String estadoEntrega,
        String cepEntrega,
        String formaPagamento,
        String codigoRastreio,
        BigDecimal subtotal,
        BigDecimal frete,
        BigDecimal total,
        List<ItemPedidoResponse> itens) {
}
