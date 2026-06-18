package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.LivroCopia;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroCopiaRepository extends JpaRepository<LivroCopia, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select copia
            from LivroCopia copia
            join fetch copia.livro livro
            join fetch copia.vendedor vendedor
            where copia.id = :id
            """)
    Optional<LivroCopia> findByIdForUpdate(@Param("id") Long id);
}
