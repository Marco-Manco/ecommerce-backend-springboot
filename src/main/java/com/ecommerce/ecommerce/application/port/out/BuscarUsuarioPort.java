package com.ecommerce.ecommerce.application.port.out;

import com.ecommerce.ecommerce.domain.model.Usuario;
import java.util.Optional;

public interface BuscarUsuarioPort {
    Usuario buscarPorEmail(String email);
    Optional<Usuario> buscarOpcionalPorEmail(String email);
    Optional<Usuario> buscarPorOAuth2(String provider, String providerId);
    boolean existeEmail(String email);
    Usuario guardar(Usuario usuario);
}
