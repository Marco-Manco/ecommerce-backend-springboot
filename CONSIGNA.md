# 🧵 Consigna Final: Ecommerce "Tejidos de Mamá"

## Objetivo

Construir un ecommerce funcional completo con Spring Boot 3 + Java 17, desplegable en producción,
que sirva como carta de presentación técnica en GitHub/LinkedIn demostrando dominio del ecosistema Spring moderno.

## Descripción del negocio

Plataforma de venta de productos textiles hechos a mano: ropa tejida, muñecos tejidos, y futura incorporación
de ropa de costura con tela. Cada producto puede tener variantes (color, talle) con stock independiente.

## Funcionalidades obligatorias

### 1. Catálogo público
- Listado de productos con paginación, filtros por categoría y precio
- Detalle de producto con variantes (color, talle), imágenes y stock disponible
- Categorías jerárquicas (ej: Ropa Tejida > Bufandas)

### 2. Usuarios y autenticación
- Registro con email + contraseña (BCrypt)
- Login con email + contraseña → devuelve JWT
- Login con Google (OAuth2) → crea/vincula cuenta → devuelve JWT
- Perfil de usuario (nombre, direcciones de envío, teléfono)
- Roles: `CLIENTE` y `ADMIN`

### 3. Carrito de compras
- Persistido en base de datos por usuario logueado
- Agregar/quitar/modificar items
- Persiste al cerrar sesión y volver (disponible cross-device)
- Validación de stock al agregar, pero sin descuento (solo intención de compra)

### 4. Checkout y pedidos
- Crear pedido desde el carrito
- Reserva temporal de stock (soft-lock por 15 min) al iniciar checkout
- Generar link de pago de MercadoPago
- Si el pago falla o expira → rollback de stock (job programado)
- Si el pago se confirma → descuento definitivo de stock
- Estados del pedido: `PENDIENTE → PAGADO → EN_PREPARACION → ENVIADO → ENTREGADO`
- Cancelación y reembolso

### 5. Pagos
- Integración con MercadoPago (Checkout Pro)
- Webhook para recibir confirmación de pago
- Registro de pagos con auditoría (raw JSON de MP)

### 6. Panel de administración
- CRUD de productos, variantes e imágenes
- Gestión de categorías
- Ver todos los pedidos, cambiar estados
- Métricas básicas (pedidos del día, productos sin stock)

### 7. Notificaciones por email (RabbitMQ)
- Email de confirmación al crear pedido
- Email de confirmación al pagar
- Email al despachar
- Alerta de stock bajo al admin

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17 + Spring Boot 3.5.x |
| Persistencia | PostgreSQL 16 + Spring Data JPA (Hibernate 6) |
| Migraciones | Flyway |
| Mapeo DTOs | MapStruct |
| Seguridad | Spring Security 6 + JWT + OAuth2 Client (Google) |
| Mensajería | RabbitMQ (eventos de dominio) |
| Documentación | SpringDoc OpenAPI (Swagger) |
| Testing | JUnit 5 + Mockito + Testcontainers (PostgreSQL, RabbitMQ) |
| Infra local | Docker Compose (PostgreSQL + RabbitMQ) |
| Deployment | Docker + Render/Railway/VPS |

## Arquitectura

Clean Architecture / Hexagonal con DDD práctico:

```
domain/          → Entidades JPA, Value Objects, Domain Events, Excepciones de dominio
application/     → DTOs (records), Casos de uso, Puertos (interfaces)
infrastructure/  → Controllers REST, JPA Repositories, Security, RabbitMQ, MercadoPago, Cloudinary
```

## Reglas de negocio clave

1. **Stock:** `stockDisponible = stock - stockReserved`. Nunca vender si `stockDisponible <= 0`.
2. **Dinero:** Siempre `BigDecimal`, NUNCA `Double` o `Float`.
3. **Carrito:** Solo guarda intención de compra. El stock se reserva al iniciar checkout.
4. **Precios snapshot:** Al crear un pedido se guarda el precio del producto en ese momento (si cambia después, el pedido mantiene el precio original).
5. **Transaccionalidad:** Checkout y pago son operaciones atómicas con `@Transactional`.
6. **Seguridad:** Toda request autenticada debe incluir `Authorization: Bearer <jwt>`.

## Entregables

1. Código fuente en GitHub
2. `README.md` con instrucciones para levantar localmente
3. API documentada con Swagger (`/swagger-ui.html`)
4. Colección de Postman o requests de prueba
5. Aplicación desplegada en Render/Railway/VPS
