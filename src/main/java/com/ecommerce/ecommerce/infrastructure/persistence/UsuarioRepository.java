package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByOauth2ProviderAndOauth2ProviderId(String provider, String providerId);
    boolean existsByEmail(String email);
}
