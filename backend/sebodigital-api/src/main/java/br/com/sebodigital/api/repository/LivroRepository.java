package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Livro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface LivroRepository extends JpaRepository<Livro, Long>, JpaSpecificationExecutor<Livro> {

    boolean existsByIsbnIgnoreCase(String isbn);

    Optional<Livro> findByIsbnIgnoreCase(String isbn);

    @Override
    @EntityGraph(attributePaths = {"editora", "vendedora", "copias", "copias.vendedor"})
    List<Livro> findAll(Specification<Livro> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = {"editora", "vendedora", "copias", "copias.vendedor"})
    Optional<Livro> findById(Long id);
}
