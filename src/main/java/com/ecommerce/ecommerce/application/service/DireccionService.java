package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.domain.model.Direccion;
import com.ecommerce.ecommerce.domain.model.Usuario;
import com.ecommerce.ecommerce.infrastructure.persistence.DireccionRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionService(DireccionRepository direccionRepository,
                            UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Direccion> listar(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return direccionRepository.findByUsuarioId(usuario.getId());
    }

    public Direccion agregar(String email, String calle, String numero, String ciudad, String provincia, String codigoPostal, String piso, String telefono) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion d = new Direccion();
        d.setUsuario(usuario);
        d.setCalle(calle);
        d.setNumero(numero);
        d.setCiudad(ciudad);
        d.setProvincia(provincia);
        d.setCodigoPostal(codigoPostal);
        d.setPiso(piso);
        d.setTelefono(telefono);
        return direccionRepository.save(d);
    }

    public Direccion actualizar(Long id, String email, String calle, String numero, String ciudad, String provincia, String codigoPostal, String piso, String telefono) {
        Direccion d = validarPertenencia(id, email);
        if (calle != null) d.setCalle(calle);
        if (numero != null) d.setNumero(numero);
        if (ciudad != null) d.setCiudad(ciudad);
        if (provincia != null) d.setProvincia(provincia);
        if (codigoPostal != null) d.setCodigoPostal(codigoPostal);
        if (piso != null) d.setPiso(piso);
        if (telefono != null) d.setTelefono(telefono);
        return direccionRepository.save(d);
    }

    public void eliminar(Long id, String email) {
        Direccion d = validarPertenencia(id, email);
        direccionRepository.delete(d);
    }

    private Direccion validarPertenencia(Long id, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Direccion d = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        if (!d.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("La dirección no pertenece a este usuario");
        }
        return d;
    }
}
