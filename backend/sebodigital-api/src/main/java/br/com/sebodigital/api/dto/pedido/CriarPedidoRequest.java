package br.com.sebodigital.api.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CriarPedidoRequest(
        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<@Valid CheckoutItemRequest> itens,

        @NotBlank(message = "Informe o endereco de entrega")
        @Size(max = 240)
        String enderecoEntrega,

        @NotBlank(message = "Informe a cidade")
        @Size(max = 120)
        String cidadeEntrega,

        @NotBlank(message = "Informe o estado")
        @Pattern(regexp = "[A-Za-z]{2}", message = "Use a sigla do estado com duas letras")
        String estadoEntrega,

        @NotBlank(message = "Informe o CEP")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP valido")
        String cepEntrega,

        @NotBlank(message = "Escolha a forma de pagamento")
        @Size(max = 40)
        String formaPagamento) {
}
