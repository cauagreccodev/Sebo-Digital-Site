package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Livro;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LivroRepository extends JpaRepository<Livro, Long>, JpaSpecificationExecutor<Livro> {

    boolean existsByIsbnIgnoreCase(String isbn);

    @Override
    @EntityGraph(attributePaths = {"editora", "vendedora", "copias", "copias.vendedor"})
    Optional<Livro> findById(Long id);
}
