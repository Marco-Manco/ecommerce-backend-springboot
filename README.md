# 🧵 Ecommerce Tejidos — Backend (Spring Boot 3 + Java 17)

API REST para ecommerce de productos tejidos artesanales. Proyecto full-stack con Spring Boot, arquitectura hexagonal, autenticación JWT + OAuth2, pasarela de pagos MercadoPago y panel de administración.

## Stack

| Capa | Tecnología |
|------|-----------|
| **Runtime** | Java 17 |
| **Framework** | Spring Boot 3.5 |
| **Persistencia** | PostgreSQL 16 + Spring Data JPA (Hibernate 6) |
| **Migraciones** | Flyway |
| **Seguridad** | Spring Security 6 + JWT + OAuth2 Client (Google) |
| **Pagos** | MercadoPago Checkout Pro (API REST) |
| **Imágenes** | Cloudinary |
| **Mensajería** | RabbitMQ (infraestructura lista, eventos pendientes) |
| **Mapeo** | MapStruct (compilado, no reflection) |
| **Documentación** | SpringDoc OpenAPI (Swagger UI) |
| **Testing** | JUnit 5 + Mockito + Testcontainers (pendiente) |
| **Infra local** | Docker Compose (PostgreSQL + RabbitMQ) |

## Arquitectura

```
com.ecommerce.ecommerce
├── domain/                    ← Entidades JPA, Value Objects, Enums, Domain Events, Excepciones
│   ├── model/                 ← 11 @Entity
│   ├── enums/                 ← Rol, EstadoPedido, EstadoPago, MetodoPago
│   ├── valueobject/           ← Email, Sku, Money (records)
│   └── exception/             ← StockInsuficienteException
│
├── application/               ← Casos de uso (orquestación)
│   ├── dto/                   ← Records de entrada/salida (~15)
│   ├── mapper/                ← MapStruct interfaces (5)
│   ├── service/               ← Implementaciones (9 services)
│   ├── port/in/               ← Interfaces de entrada
│   └── port/out/              ← Puertos de salida (BuscarUsuarioPort, PagoPort, etc.)
│
├── infrastructure/            ← Adaptadores (todo lo externo)
│   ├── config/                ← SecurityConfig, BeansConfig, DataSeeder
│   ├── persistence/           ← Spring Data JPA repositories + Specifications
│   ├── web/                   ← @RestController (10 controllers)
│   ├── security/              ← JwtService, JwtAuthFilter, OAuth2SuccessHandler
│   ├── payment/               ← MercadoPagoAdapter
│   ├── storage/               ← CloudinaryAdapter
│   └── scheduler/             ← ReservaExpiradaScheduler
```

**Principios aplicados:**
- **Dependency Inversion**: Los services dependen de interfaces (ports), no de implementaciones concretas
- **Domain isolation**: `domain/` no importa nada de `application/` ni `infrastructure/`
- **SOLID**: Cada service tiene una sola responsabilidad
- **CQRS ligero**: Specifications para queries complejas, services para comandos

## Cómo levantar

### Requisitos
- JDK 17
- Docker Desktop
- Node.js (para el frontend)

### 1. Infraestructura
```bash
docker-compose up -d
```
Esto levanta PostgreSQL 16 (puerto 5432) y RabbitMQ (puertos 5672/15672).

### 2. Variables de entorno (IntelliJ Run Config)
```
JWT_SECRET=un-secreto-largo-de-al-menos-32-caracteres-ok
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
MP_ACCESS_TOKEN=APP_USR-...
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

### 3. Ejecutar
```bash
./mvnw spring-boot:run
```
O ejecutar `EcommerceApplication` desde IntelliJ.

### 4. Swagger UI
```
http://localhost:8080/swagger-ui.html
```

Flyway ejecuta las migraciones automáticamente al iniciar. El `DataSeeder` (solo con profile `dev`) inserta datos de prueba: 5 categorías, 7 productos, 18 variantes y un usuario `cliente@test.com` / `test1234` con direcciones.

## Funcionalidades

### Autenticación
- [x] Registro con email + contraseña (BCrypt)
- [x] Login con email + contraseña → JWT
- [x] Login con Google OAuth2 → JWT propio
- [x] JWT stateless en cada request
- [x] Protección de endpoints con `@PreAuthorize`

### Catálogo
- [x] Listado con paginación y filtros dinámicos (nombre, categoría, rango de precios)
- [x] Detalle con variantes (color, talle, stock) e imágenes
- [x] Árbol de categorías jerárquico

### Carrito
- [x] CRUD de items sincronizado con base de datos
- [x] Validación de stock (sin descuento)
- [x] Cálculo de totales

### Checkout y Pedidos
- [x] Checkout con lock pesimista (`SELECT ... FOR UPDATE`)
- [x] Reserva temporal de stock por 15 minutos
- [x] Scheduler que libera reservas expiradas
- [x] Snapshot de precios en `ItemPedido`
- [x] Número de pedido auto-generado (`ORD-YYYYMMDD-NNN`)
- [x] Historial de pedidos del usuario

### Pagos
- [x] Integración MercadoPago Checkout Pro (sin SDK externo, via REST)
- [x] Generación de link de pago real
- [x] Verificación de pago con API de MP antes de confirmar
- [x] Auto-redirect post-pago (con ngrok en desarrollo, HTTPS en producción)
- [x] Webhook preparado para IPN de MercadoPago

### Panel Admin
- [x] CRUD de productos con variantes (precio, stock, color, talle) e imágenes
- [x] CRUD de categorías con árbol jerárquico
- [x] Gestión de pedidos: filtros (estado, búsqueda, fechas), cambio de estado (PENDIENTE → PAGADO → EN PREPARACIÓN → ENVIADO → ENTREGADO), cancelación
- [x] Subida de imágenes a Cloudinary

### Usuario
- [x] Perfil con datos personales
- [x] CRUD de direcciones de envío

## API — Endpoints principales

| Método | Endpoint | Rol |
|--------|----------|:---:|
| `POST` | `/api/auth/register` | Público |
| `POST` | `/api/auth/login` | Público |
| `GET` | `/api/productos` | Público |
| `GET` | `/api/productos/{id}` | Público |
| `GET` | `/api/categorias` | Público |
| `GET` | `/api/carrito` | CLIENTE |
| `POST` | `/api/carrito/items` | CLIENTE |
| `POST` | `/api/checkout` | CLIENTE |
| `GET` | `/api/pedidos` | CLIENTE |
| `GET` | `/api/pagos/confirmar` | Público (redirect MP) |
| `POST` | `/api/pagos/mercadopago/notify` | Público (webhook) |
| `GET/POST/PUT` | `/api/admin/productos` | ADMIN |
| `GET/POST/PUT` | `/api/admin/categorias` | ADMIN |
| `GET` | `/api/admin/pedidos` | ADMIN |
| `PATCH` | `/api/admin/pedidos/{id}/estado` | ADMIN |
| `GET/POST/PUT/DELETE` | `/api/usuarios/me/direcciones` | CLIENTE/ADMIN |

Documentación completa en Swagger UI: `/swagger-ui.html`

## Modelo de datos

```
Usuario 1──N Direccion
Usuario 1──1 Carrito
Carrito 1──N ItemCarrito
ItemCarrito N──1 VarianteProducto

Usuario 1──N Pedido
Pedido 1──N ItemPedido
ItemPedido N──1 VarianteProducto
Pedido 1──1 Pago

Producto 1──N VarianteProducto
Producto 1──N ImagenProducto
Producto N──1 Categoria
Categoria 1──N Categoria (autoreferencia jerárquica)
```

## Próximos pasos

- [ ] RabbitMQ: eventos de dominio (PedidoCreadoEvent, PedidoPagadoEvent, StockBajoEvent)
- [ ] Emails transaccionales (pedido creado, pagado, enviado)
- [ ] Testing con Testcontainers (PostgreSQL + RabbitMQ reales)
- [ ] Integración WhatsApp Cloud API para gestión de pedidos

## 🌐 Demo en vivo

| Recurso | URL |
|----------|-----|
| **Frontend** | [eccomerce-frontend-react.vercel.app](https://eccomerce-frontend-react.vercel.app) |
| **API Swagger** | [ecommerce-backend-springboot-sb48.onrender.com/swagger-ui.html](https://ecommerce-backend-springboot-sb48.onrender.com/swagger-ui.html) |

## 🔑 Credenciales de prueba

| Rol | Email | Contraseña |
|-----|-------|------------|
| **Admin** | `admin@ecommerce.com` | `admin1234` |

## 💰 Probar pagos (MercadoPago Sandbox)

1. Agregá productos al carrito y hacé checkout
2. En la pantalla de MercadoPago, logueate con el usuario de prueba (sandbox):
   - **Usuario:** `TESTUSER3477234149032093924`
   - **Contraseña:** `NUD38I13vV`
3. Pagá con cualquier medio de pago disponible en el sandbox (no se mueve dinero real)
4. Después del pago serás redirigido automáticamente a la app

> ⚠️ Estamos en **sandbox de MercadoPago**. No se realizan cobros reales.
