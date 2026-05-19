package com.ecommerce.ecommerce.application.mapper;

import com.ecommerce.ecommerce.application.dto.AuthResponse;
import com.ecommerce.ecommerce.application.dto.RegistroRequest;
import com.ecommerce.ecommerce.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    default AuthResponse toAuthResponse(String token, Usuario usuario) {
        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol().name()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "rol", constant = "CLIENTE")
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "oauth2Provider", ignore = true)
    @Mapping(target = "oauth2ProviderId", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "ultimoAcceso", ignore = true)
    @Mapping(target = "direcciones", ignore = true)
    @Mapping(target = "carrito", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    Usuario toEntity(RegistroRequest request);
}