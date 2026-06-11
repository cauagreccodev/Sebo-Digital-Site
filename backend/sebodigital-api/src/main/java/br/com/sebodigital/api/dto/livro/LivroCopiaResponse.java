package br.com.sebodigital.api.dto.livro;

import br.com.sebodigital.api.model.enums.EstadoConservacao;
import br.com.sebodigital.api.model.enums.TipoCopia;
import java.math.BigDecimal;

public record LivroCopiaResponse(
        Long id,
        String vendedor,
        String cidadeVendedor,
        BigDecimal avaliacaoVendedor,
        TipoCopia tipo,
        EstadoConservacao estadoConservacao,
        BigDecimal preco,
        Integer estoque,
        String cidade,
        boolean freteGratis,
        boolean promocao,
        boolean compraCorporativa,
        boolean ativo) {
}
