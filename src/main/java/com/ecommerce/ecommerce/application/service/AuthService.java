package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.AuthResponse;
import com.ecommerce.ecommerce.application.dto.LoginRequest;
import com.ecommerce.ecommerce.application.dto.RegistroRequest;
import com.ecommerce.ecommerce.application.mapper.UsuarioMapper;
import com.ecommerce.ecommerce.application.port.out.BuscarUsuarioPort;
import com.ecommerce.ecommerce.domain.enums.Rol;
import com.ecommerce.ecommerce.domain.model.Usuario;
import com.ecommerce.ecommerce.infrastructure.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final BuscarUsuarioPort buscarUsuarioPort;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(BuscarUsuarioPort buscarUsuarioPort,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       UsuarioMapper usuarioMapper) {
        this.buscarUsuarioPort = buscarUsuarioPort;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (buscarUsuarioPort.existeEmail(request.email())) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario = buscarUsuarioPort.guardar(usuario);

        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(request.email());

        if (usuario.getPasswordHash() == null || !passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        if (!usuario.getActivo()) {
            throw new BadCredentialsException("Usuario desactivado");
        }

        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }

    @Transactional
    public AuthResponse procesarOAuth2Login(String email, String nombre, String googleId) {
        Usuario usuario = buscarUsuarioPort.buscarPorOAuth2("GOOGLE", googleId)
                .orElseGet(() -> {
                    var existente = buscarUsuarioPort.buscarOpcionalPorEmail(email)
                            .orElse(new Usuario());
                    existente.setEmail(email);
                    existente.setNombre(nombre);
                    existente.setOauth2Provider("GOOGLE");
                    existente.setOauth2ProviderId(googleId);
                    existente.setRol(Rol.CLIENTE);
                    existente.setActivo(true);
                    return buscarUsuarioPort.guardar(existente);
                });

        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }
}
