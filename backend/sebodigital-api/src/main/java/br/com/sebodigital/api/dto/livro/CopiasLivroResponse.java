package br.com.sebodigital.api.dto.livro;

import java.util.List;

public record CopiasLivroResponse(
        List<LivroCopiaResponse> novas,
        List<LivroCopiaResponse> usadas) {
}
