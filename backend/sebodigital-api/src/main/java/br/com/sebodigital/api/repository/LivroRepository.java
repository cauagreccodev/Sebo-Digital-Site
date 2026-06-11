package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Livro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbnIgnoreCase(String isbn);

    @Override
    @EntityGraph(attributePaths = {"editora", "vendedora", "copias", "copias.vendedor"})
    Optional<Livro> findById(Long id);

    @EntityGraph(attributePaths = {"editora", "vendedora", "copias", "copias.vendedor"})
    @Query("""
            select distinct livro
            from Livro livro
            where (:busca is null
                or lower(livro.titulo) like lower(concat('%', :busca, '%'))
                or lower(livro.autor) like lower(concat('%', :busca, '%'))
                or lower(livro.isbn) like lower(concat('%', :busca, '%')))
              and (:categoria is null or lower(livro.categoria) = lower(:categoria))
              and (:freteGratis is null or livro.destaqueFreteGratis = :freteGratis)
              and (:oferta is null or livro.destaqueOferta = :oferta)
              and (:maisVendido is null or livro.destaqueMaisVendido = :maisVendido)
              and (:lancamento is null or livro.destaqueLancamento = :lancamento)
            order by livro.titulo
            """)
    List<Livro> buscar(
            @Param("busca") String busca,
            @Param("categoria") String categoria,
            @Param("freteGratis") Boolean freteGratis,
            @Param("oferta") Boolean oferta,
            @Param("maisVendido") Boolean maisVendido,
            @Param("lancamento") Boolean lancamento);
}
