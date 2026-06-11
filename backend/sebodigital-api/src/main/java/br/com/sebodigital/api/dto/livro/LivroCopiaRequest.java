package br.com.sebodigital.api.dto.livro;

import br.com.sebodigital.api.model.enums.EstadoConservacao;
import br.com.sebodigital.api.model.enums.TipoCopia;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record LivroCopiaRequest(
        @NotBlank(message = "Vendedora e obrigatoria")
        @Size(max = 120, message = "Vendedora deve ter no maximo 120 caracteres")
        String vendedor,

        @Size(max = 120, message = "Cidade da vendedora deve ter no maximo 120 caracteres")
        String cidadeVendedor,

        @DecimalMin(value = "0.0", message = "Avaliacao deve ser maior ou igual a 0")
        @DecimalMax(value = "5.0", message = "Avaliacao deve ser menor ou igual a 5")
        BigDecimal avaliacaoVendedor,

        @NotNull(message = "Tipo da copia e obrigatorio")
        TipoCopia tipo,

        EstadoConservacao estadoConservacao,

        @NotNull(message = "Preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Estoque e obrigatorio")
        @Min(value = 0, message = "Estoque nao pode ser negativo")
        Integer estoque,

        @NotBlank(message = "Cidade da oferta e obrigatoria")
        @Size(max = 120, message = "Cidade da oferta deve ter no maximo 120 caracteres")
        String cidade,

        Boolean freteGratis,
        Boolean promocao,
        Boolean compraCorporativa,
        Boolean ativo) {
}
