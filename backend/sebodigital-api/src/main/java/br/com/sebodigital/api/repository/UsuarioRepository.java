package br.com.sebodigital.api.repository;

import br.com.sebodigital.api.model.enums.AuthProvider;
import br.com.sebodigital.api.model.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    boolean existsByEmailIgnoreCase(String email);
}
