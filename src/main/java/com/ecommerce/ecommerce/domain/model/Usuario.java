package com.ecommerce.ecommerce.domain.model;

import com.ecommerce.ecommerce.domain.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "oauth2_provider", length = 20)
    private String oauth2Provider;

    @Column(name = "oauth2_provider_id", length = 255)
    private String oauth2ProviderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "rol_usuario")
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(lombok.AccessLevel.NONE)
    private List<Direccion> direcciones = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(lombok.AccessLevel.NONE)
    private Carrito carrito;

    @OneToMany(mappedBy = "usuario")
    @Setter(lombok.AccessLevel.NONE)
    private List<Pedido> pedidos = new ArrayList<>();

    public Usuario(String nombre, String email, String passwordHash, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }
}
