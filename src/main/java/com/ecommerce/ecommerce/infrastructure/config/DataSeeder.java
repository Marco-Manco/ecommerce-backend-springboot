package com.ecommerce.ecommerce.infrastructure.config;

import com.ecommerce.ecommerce.domain.enums.Rol;
import com.ecommerce.ecommerce.domain.model.*;
import com.ecommerce.ecommerce.infrastructure.persistence.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoriaRepository categoriaRepository,
                      ProductoRepository productoRepository,
                      VarianteProductoRepository varianteProductoRepository,
                      UsuarioRepository usuarioRepository,
                      DireccionRepository direccionRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.varianteProductoRepository = varianteProductoRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            return;
        }

        Categoria tejidos = crearCategoria("Ropa Tejida", "Prendas tejidas a mano con lana merino", null);
        Categoria bufandas = crearCategoria("Bufandas", "Bufandas de lana y algodón", tejidos);
        Categoria abrigos = crearCategoria("Abrigos", "Abrigos y ponchos tejidos", tejidos);
        Categoria munecos = crearCategoria("Muñecos", "Muñecos tejidos artesanales", null);
        Categoria accesorios = crearCategoria("Accesorios", "Gorros, guantes y más", tejidos);

        Producto bufandaLana = crearProducto("Bufanda de lana merino", "Bufanda tejida a mano con lana merino 100%. Suave, calentita y con diseño artesanal único.", bufandas);
        Producto bufandaAlgodon = crearProducto("Bufanda de algodón", "Bufanda liviana de algodón orgánico. Ideal para media estación.", bufandas);
        Producto poncho = crearProducto("Poncho tejido", "Poncho tradicional tejido en telar. Perfecto para los días fríos de invierno.", abrigos);
        Producto osoTejido = crearProducto("Osito tejido", "Oso de peluche tejido a mano con relleno hipoalergénico. 30 cm de alto.", munecos);
        Producto conejoTejido = crearProducto("Conejito tejido", "Conejo de peluche artesanal con detalles bordados. 25 cm.", munecos);
        Producto gorro = crearProducto("Gorro tejido", "Gorro de lana con pompón. Varios colores disponibles.", accesorios);
        Producto guantes = crearProducto("Guantes tejidos", "Guantes sin dedos tejidos con lana merino. Cálidos y prácticos.", accesorios);

        crearVariante(bufandaLana, "BUF-LAN-ROJ-M", "Rojo", "M", new BigDecimal("1500.00"), 10);
        crearVariante(bufandaLana, "BUF-LAN-ROJ-L", "Rojo", "L", new BigDecimal("1700.00"), 8);
        crearVariante(bufandaLana, "BUF-LAN-AZU-M", "Azul", "M", new BigDecimal("1500.00"), 5);
        crearVariante(bufandaLana, "BUF-LAN-AZU-L", "Azul", "L", new BigDecimal("1700.00"), 6);
        crearVariante(bufandaLana, "BUF-LAN-NEG-M", "Negro", "M", new BigDecimal("1500.00"), 4);
        crearVariante(bufandaAlgodon, "BUF-ALG-BLA-U", "Blanco", "Único", new BigDecimal("1200.00"), 15);
        crearVariante(bufandaAlgodon, "BUF-ALG-CRU-U", "Crudo", "Único", new BigDecimal("1200.00"), 12);
        crearVariante(poncho, "PON-ROJ-U", "Rojo", "Único", new BigDecimal("4500.00"), 5);
        crearVariante(poncho, "PON-MAR-U", "Marrón", "Único", new BigDecimal("4500.00"), 3);
        crearVariante(poncho, "PON-NEG-U", "Negro", "Único", new BigDecimal("4800.00"), 4);
        crearVariante(osoTejido, "OSO-MAR-U", "Marrón", "Único", new BigDecimal("3500.00"), 7);
        crearVariante(conejoTejido, "CON-BLA-U", "Blanco", "Único", new BigDecimal("3200.00"), 6);
        crearVariante(conejoTejido, "CON-GRI-U", "Gris", "Único", new BigDecimal("3200.00"), 4);
        crearVariante(gorro, "GOR-ROJ-U", "Rojo", "Único", new BigDecimal("900.00"), 20);
        crearVariante(gorro, "GOR-AZU-U", "Azul", "Único", new BigDecimal("900.00"), 18);
        crearVariante(gorro, "GOR-VER-U", "Verde", "Único", new BigDecimal("950.00"), 15);
        crearVariante(guantes, "GUA-GRI-U", "Gris", "Único", new BigDecimal("800.00"), 12);
        crearVariante(guantes, "GUA-NEG-U", "Negro", "Único", new BigDecimal("800.00"), 10);

        crearUsuarioDePrueba();
    }

    private Categoria crearCategoria(String nombre, String descripcion, Categoria padre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setCategoriaPadre(padre);
        return categoriaRepository.save(c);
    }

    private Producto crearProducto(String nombre, String descripcion, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setCategoria(categoria);
        return productoRepository.save(p);
    }

    private void crearVariante(Producto producto, String sku, String color, String talle, BigDecimal precio, int stock) {
        VarianteProducto v = new VarianteProducto();
        v.setProducto(producto);
        v.setSku(sku);
        v.setColor(color);
        v.setTamanio(talle);
        v.setPrecio(precio);
        v.setStock(stock);
        varianteProductoRepository.save(v);
    }

    private void crearUsuarioDePrueba() {
        if (usuarioRepository.existsByEmail("cliente@test.com")) return;

        Usuario cliente = new Usuario();
        cliente.setEmail("cliente@test.com");
        cliente.setNombre("María Cliente");
        cliente.setPasswordHash(passwordEncoder.encode("test1234"));
        cliente.setRol(Rol.CLIENTE);
        cliente = usuarioRepository.save(cliente);

        Direccion dir1 = new Direccion();
        dir1.setUsuario(cliente);
        dir1.setCalle("Av. Santa Fe");
        dir1.setNumero("1234");
        dir1.setCiudad("Buenos Aires");
        dir1.setProvincia("CABA");
        dir1.setCodigoPostal("C1425");
        dir1.setEsPrincipal(true);
        direccionRepository.save(dir1);

        Direccion dir2 = new Direccion();
        dir2.setUsuario(cliente);
        dir2.setCalle("Calle Corrientes");
        dir2.setNumero("5678");
        dir2.setPiso("3B");
        dir2.setCiudad("Buenos Aires");
        dir2.setProvincia("CABA");
        dir2.setCodigoPostal("C1043");
        direccionRepository.save(dir2);

        if (usuarioRepository.existsByEmail("admin@test.com")) return;

        Usuario admin = new Usuario();
        admin.setEmail("admin@test.com");
        admin.setNombre("Admin Principal");
        admin.setPasswordHash(passwordEncoder.encode("admin1234"));
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);
    }
}
