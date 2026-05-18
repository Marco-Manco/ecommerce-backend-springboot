-- =============================================
-- V1__init.sql — Schema inicial del ecommerce
-- =============================================
-- ENUMS
CREATE TYPE rol_usuario AS ENUM ('CLIENTE', 'ADMIN');
CREATE TYPE metodo_pago AS ENUM ('MERCADOPAGO');
CREATE TYPE estado_pedido AS ENUM ('PENDIENTE', 'PAGADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO', 'REEMBOLSADO');
CREATE TYPE estado_pago AS ENUM ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'REEMBOLSADO');

-- =============================================
-- CATEGORIA (jerárquica)
-- =============================================
CREATE TABLE categoria (
   id BIGSERIAL PRIMARY KEY,
   nombre VARCHAR(100) NOT NULL,
   descripcion TEXT,
   categoria_padre_id BIGINT,
   activo BOOLEAN NOT NULL DEFAULT TRUE,
   fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
   CONSTRAINT fk_categoria_padre FOREIGN KEY (categoria_padre_id) REFERENCES categoria(id)
);
CREATE INDEX idx_categoria_padre ON categoria(categoria_padre_id);

-- =============================================
-- USUARIO
-- =============================================
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    oauth2_provider VARCHAR(20),
    oauth2_provider_id VARCHAR(255),
    rol rol_usuario NOT NULL DEFAULT 'CLIENTE',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    ultimo_acceso TIMESTAMP
);
CREATE UNIQUE INDEX idx_usuario_email ON usuario(email);

-- =============================================
-- DIRECCION
-- =============================================
CREATE TABLE direccion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    calle VARCHAR(200) NOT NULL,
    numero VARCHAR(10),
    piso VARCHAR(10),
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    telefono VARCHAR(20),
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_direccion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
CREATE INDEX idx_direccion_usuario ON direccion(usuario_id);

-- =============================================
-- PRODUCTO
-- =============================================
CREATE TABLE producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria_id BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
    );
CREATE INDEX idx_producto_categoria ON producto(categoria_id);

-- =============================================
-- VARIANTE_PRODUCTO (colores, talles, stock individual)
-- =============================================
CREATE TABLE variante_producto (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(50),
    tamanio VARCHAR(20),
    precio NUMERIC(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_reservado INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_variante_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT chk_stock_no_negativo CHECK (stock >= 0),
    CONSTRAINT chk_stock_reservado_no_negativo CHECK (stock_reservado >= 0),
    CONSTRAINT chk_precio_positivo CHECK (precio > 0)
);
CREATE INDEX idx_variante_producto ON variante_producto(producto_id);
CREATE UNIQUE INDEX idx_variante_sku ON variante_producto(sku);

-- =============================================
-- IMAGEN_PRODUCTO
-- =============================================
CREATE TABLE imagen_producto (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    orden INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_imagen_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);
CREATE INDEX idx_imagen_producto ON imagen_producto(producto_id);

-- =============================================
-- CARRITO
-- =============================================
CREATE TABLE carrito (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- =============================================
-- ITEM_CARRITO (solo intención de compra, NO descuenta stock)
-- =============================================
CREATE TABLE item_carrito (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    variante_producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_item_carrito_carrito FOREIGN KEY (carrito_id) REFERENCES carrito(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_carrito_variante FOREIGN KEY (variante_producto_id) REFERENCES variante_producto(id),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
    UNIQUE (carrito_id, variante_producto_id)
);

-- =============================================
-- PEDIDO
-- =============================================
CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,
    numero_pedido VARCHAR(30) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    direccion_envio_id BIGINT NOT NULL,
    estado estado_pedido NOT NULL DEFAULT 'PENDIENTE',
    metodo_pago metodo_pago NOT NULL DEFAULT 'MERCADOPAGO',
    subtotal NUMERIC(12,2) NOT NULL,
    costo_envio NUMERIC(12,2) NOT NULL DEFAULT 0,
    total NUMERIC(12,2) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_expiracion_reserva TIMESTAMP,
    fecha_pago TIMESTAMP,
    fecha_envio TIMESTAMP,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (direccion_envio_id) REFERENCES direccion(id),
    CONSTRAINT chk_total_positivo CHECK (total >= 0)
);
CREATE INDEX idx_pedido_usuario ON pedido(usuario_id);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_pedido_numero ON pedido(numero_pedido);

-- =============================================
-- ITEM_PEDIDO (snapshot del producto al comprar)
-- =============================================
CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    variante_producto_id BIGINT,
    producto_nombre VARCHAR(200) NOT NULL,
    variante_descripcion VARCHAR(200),
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_item_pedido_variante FOREIGN KEY (variante_producto_id) REFERENCES variante_producto(id),
    CONSTRAINT chk_cantidad_item_positiva CHECK (cantidad > 0)
);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(pedido_id);

-- =============================================
-- PAGO
-- =============================================
CREATE TABLE pago (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    mercadopago_payment_id VARCHAR(100),
    estado estado_pago NOT NULL DEFAULT 'PENDIENTE',
    monto NUMERIC(12,2) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    raw_response JSONB,
    CONSTRAINT fk_pago_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);
CREATE INDEX idx_pago_pedido ON pago(pedido_id);
CREATE INDEX idx_pago_mercadopago ON pago(mercadopago_payment_id);