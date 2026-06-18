package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.entity.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"itens", "itens.livro"})
    List<Pedido> findByUsuarioEmailIgnoreCaseOrderByCriadoEmDesc(String email);

    @EntityGraph(attributePaths = {"itens", "itens.livro"})
    Optional<Pedido> findByIdAndUsuarioEmailIgnoreCase(Long id, String email);
}
