package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.service.DireccionService;
import com.ecommerce.ecommerce.domain.model.Direccion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios/me")
@Tag(name = "Usuario", description = "Perfil y direcciones del usuario")
@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
public class UsuarioController {

    private final DireccionService direccionService;

    public UsuarioController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping("/direcciones")
    @Operation(summary = "Listar direcciones del usuario")
    public ResponseEntity<List<Map<String, Object>>> listarDirecciones(@AuthenticationPrincipal UserDetails userDetails) {
        var direcciones = direccionService.listar(userDetails.getUsername()).stream().map(d -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("calle", d.getCalle());
            m.put("numero", d.getNumero());
            m.put("piso", d.getPiso());
            m.put("ciudad", d.getCiudad());
            m.put("provincia", d.getProvincia());
            m.put("codigoPostal", d.getCodigoPostal());
            m.put("telefono", d.getTelefono());
            m.put("esPrincipal", d.getEsPrincipal());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(direcciones);
    }

    @PostMapping("/direcciones")
    @Operation(summary = "Agregar una dirección")
    public ResponseEntity<Map<String, Object>> agregar(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody Map<String, String> body) {
        var d = direccionService.agregar(
            userDetails.getUsername(),
            body.get("calle"),
            body.get("numero"),
            body.get("ciudad"),
            body.get("provincia"),
            body.get("codigoPostal"),
            body.get("piso"),
            body.get("telefono")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(d));
    }

    @PutMapping("/direcciones/{id}")
    @Operation(summary = "Actualizar una dirección")
    public ResponseEntity<Map<String, Object>> actualizar(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable Long id,
                                                           @RequestBody Map<String, String> body) {
        var d = direccionService.actualizar(
            id, userDetails.getUsername(),
            body.get("calle"), body.get("numero"), body.get("ciudad"),
            body.get("provincia"), body.get("codigoPostal"),
            body.get("piso"), body.get("telefono")
        );
        return ResponseEntity.ok(toMap(d));
    }

    @DeleteMapping("/direcciones/{id}")
    @Operation(summary = "Eliminar una dirección")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long id) {
        direccionService.eliminar(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toMap(Direccion d) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", d.getId());
        m.put("calle", d.getCalle());
        m.put("numero", d.getNumero());
        m.put("piso", d.getPiso());
        m.put("ciudad", d.getCiudad());
        m.put("provincia", d.getProvincia());
        m.put("codigoPostal", d.getCodigoPostal());
        m.put("telefono", d.getTelefono());
        m.put("esPrincipal", d.getEsPrincipal());
        return m;
    }
}
