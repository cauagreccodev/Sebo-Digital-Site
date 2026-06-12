package br.com.sebodigital.api.dto.livro;

import java.math.BigDecimal;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String autorImagemUrl,
        String editora,
        String vendedora,
        String isbn,
        String idioma,
        Integer anoPublicacao,
        String categoria,
        String descricao,
        String imagemUrl,
        DestaquesLivroResponse destaques,
        CopiasLivroResponse copias,
        BigDecimal menorPreco,
        Integer estoqueTotal) {
}
