package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.AuthResponse;
import com.ecommerce.ecommerce.application.dto.LoginRequest;
import com.ecommerce.ecommerce.application.dto.RegistroRequest;
import com.ecommerce.ecommerce.application.mapper.UsuarioMapper;
import com.ecommerce.ecommerce.domain.enums.Rol;
import com.ecommerce.ecommerce.domain.model.Usuario;
import com.ecommerce.ecommerce.infrastructure.persistence.UsuarioRepository;
import com.ecommerce.ecommerce.infrastructure.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper
    ){
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public AuthResponse registrar(RegistroRequest request){
        if(usuarioRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        if(!passwordEncoder.matches(request.password(), usuario.getPasswordHash())){
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        if(!usuario.getActivo()){
            throw new BadCredentialsException("Usuario desactivado");
        }

        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }

    @Transactional
    public AuthResponse procesarOAuth2Login(String email, String nombre, String googleId) {
        Usuario usuario = usuarioRepository
                .findByOauth2ProviderAndOauth2ProviderId("GOOGLE", googleId)
                .orElseGet(() -> {
                    var existente = usuarioRepository.findByEmail(email).orElse(new Usuario());
                    existente.setEmail(email);
                    existente.setNombre(nombre);
                    existente.setOauth2Provider("GOOGLE");
                    existente.setOauth2ProviderId(googleId);
                    existente.setRol(Rol.CLIENTE);
                    existente.setActivo(true);
                    return usuarioRepository.save(existente);
                });
        String token = jwtService.generateToken(usuario);
        return usuarioMapper.toAuthResponse(token, usuario);
    }
}
