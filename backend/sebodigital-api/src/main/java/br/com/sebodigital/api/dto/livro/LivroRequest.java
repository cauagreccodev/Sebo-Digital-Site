package br.com.sebodigital.api.dto.livro;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record LivroRequest(
        @NotBlank(message = "Titulo e obrigatorio")
        @Size(max = 180, message = "Titulo deve ter no maximo 180 caracteres")
        String titulo,

        @NotBlank(message = "Autor e obrigatorio")
        @Size(max = 140, message = "Autor deve ter no maximo 140 caracteres")
        String autor,

        @NotBlank(message = "Editora e obrigatoria")
        @Size(max = 120, message = "Editora deve ter no maximo 120 caracteres")
        String editora,

        @Size(max = 120, message = "Vendedora deve ter no maximo 120 caracteres")
        String vendedora,

        @Size(max = 120, message = "Cidade da vendedora deve ter no maximo 120 caracteres")
        String cidadeVendedora,

        @DecimalMin(value = "0.0", message = "Avaliacao deve ser maior ou igual a 0")
        @DecimalMax(value = "5.0", message = "Avaliacao deve ser menor ou igual a 5")
        BigDecimal avaliacaoVendedora,

        @Size(max = 30, message = "ISBN deve ter no maximo 30 caracteres")
        String isbn,

        @NotBlank(message = "Idioma e obrigatorio")
        @Size(max = 50, message = "Idioma deve ter no maximo 50 caracteres")
        String idioma,

        Integer anoPublicacao,

        @NotBlank(message = "Categoria e obrigatoria")
        @Size(max = 80, message = "Categoria deve ter no maximo 80 caracteres")
        String categoria,

        @Size(max = 1000, message = "Descricao deve ter no maximo 1000 caracteres")
        String descricao,

        @NotBlank(message = "URL da imagem e obrigatoria")
        @Size(max = 600, message = "URL da imagem deve ter no maximo 600 caracteres")
        String imagemUrl,

        @Size(max = 600, message = "URL da imagem do autor deve ter no maximo 600 caracteres")
        String autorImagemUrl,

        boolean destaqueFreteGratis,
        boolean destaqueOferta,
        boolean destaqueMaisVendido,
        boolean destaqueLancamento,

        @Valid
        @NotEmpty(message = "Informe ao menos uma copia/oferta do livro")
        List<LivroCopiaRequest> copias) {

    public LivroRequest(
            String titulo,
            String autor,
            String editora,
            String vendedora,
            String cidadeVendedora,
            BigDecimal avaliacaoVendedora,
            String isbn,
            String idioma,
            Integer anoPublicacao,
            String categoria,
            String descricao,
            String imagemUrl,
            boolean destaqueFreteGratis,
            boolean destaqueOferta,
            boolean destaqueMaisVendido,
            boolean destaqueLancamento,
            List<LivroCopiaRequest> copias) {
        this(
                titulo,
                autor,
                editora,
                vendedora,
                cidadeVendedora,
                avaliacaoVendedora,
                isbn,
                idioma,
                anoPublicacao,
                categoria,
                descricao,
                imagemUrl,
                null,
                destaqueFreteGratis,
                destaqueOferta,
                destaqueMaisVendido,
                destaqueLancamento,
                copias);
    }
}
