package br.com.sebodigital.api.dto.livro;

public record DestaquesLivroResponse(
        boolean freteGratis,
        boolean oferta,
        boolean maisVendido,
        boolean lancamento) {
}
