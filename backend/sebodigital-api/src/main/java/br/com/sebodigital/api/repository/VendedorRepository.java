package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Vendedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    Optional<Vendedor> findByNomeIgnoreCaseAndCidadeIgnoreCase(String nome, String cidade);
}
