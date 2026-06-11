package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Editora;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditoraRepository extends JpaRepository<Editora, Long> {

    Optional<Editora> findByNomeIgnoreCase(String nome);
}
