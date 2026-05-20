package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.port.out.BuscarUsuarioPort;
import com.ecommerce.ecommerce.domain.model.Usuario;
import com.ecommerce.ecommerce.infrastructure.persistence.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsuarioService implements BuscarUsuarioPort {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    @Override
    public Optional<Usuario> buscarOpcionalPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Usuario> buscarPorOAuth2(String provider, String providerId) {
        return usuarioRepository.findByOauth2ProviderAndOauth2ProviderId(provider, providerId);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
