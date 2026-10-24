# Colección Postman — Joyería Morgan

## Contenido

| Archivo | Uso |
|---|---|
| `Joyeria-Morgan-DEMO.postman_collection.json` | 39 peticiones en 9 carpetas, ~6 s, sin environment |

Todas las URLs y variables están dentro de la colección.

## Importar y ejecutar

1. Postman → **Import** → **File** → `postman/Joyeria-Morgan-DEMO.postman_collection.json`.
2. Abrir la colección y usar **Run collection**.
3. Mantener el orden de las carpetas.

## Requisitos

- MySQL 5510, RabbitMQ 5672, Kafka 9092 y Kafka UI 8090.
- Eureka 8761, Config Server 8888, los cinco backends (8081-8085) y el gateway 8762.
- Usuarios semilla: `admin/admin123` (ADMIN) y `vendedor/vendedor123` (VENDEDOR).

## Carpetas

| Carpeta | Contenido |
|---|---|
| `01. Seguridad y roles (T4)` | Login, JWT, 401 sin token, 403 por rol y listado de usuarios |
| `02. CRUD inventario (8081)` | CRUD de joyas, 404, 400 por precio inválido y 400 por stock negativo |
| `03. CRUD clientes (8084)` | CRUD de clientes |
| `04. Ventas: Feign + Kafka (T1/T2)` | Total por Feign, publicación en Kafka, detalle, 404, 400 y 409 |
| `05. Resiliencia (T5)` | Circuit Breaker, Retry y estado del circuito |
| `06. RabbitMQ y stock final (T2)` | Reserva por RabbitMQ, colas y verificación de stock |
| `07. Eureka, gateway y reportes (T3/T1)` | Servicios registrados, gateway y reportes |
| `08. Observabilidad (T7)` | Health y métricas de actuator |
| `09. Limpieza` | Anulación de ventas, devolución de stock y borrado de datos temporales |

## Roles

| Recurso | ADMIN | VENDEDOR |
|---|---|---|
| `GET /products`, `GET /products/{id}` | ✅ | ✅ |
| `POST /products`, `PUT /products/{id}`, `DELETE /products/{id}` | ✅ | ❌ 403 |
| `GET /customers`, `POST /customers`, `PUT /customers/{id}` | ✅ | ✅ |
| `DELETE /customers/{id}` | ✅ | ❌ 403 |
| `GET /sales`, `POST /sales`, `PUT /sales/{id}` | ✅ | ✅ |
| `DELETE /sales/{id}` | ✅ | ❌ 403 |
| `GET /reports/**` | ✅ | ❌ 403 |
| `GET/POST/PUT/DELETE /users` | ✅ | ❌ 403 |

## Datos de prueba

- Cada corrida genera un `runId` y crea una joya, un cliente y dos ventas temporales.
- Las ventas usan solo joyas temporales, por lo que no consumen stock del catálogo real.
- La carpeta `09. Limpieza` anula las ventas, verifica la devolución del stock y elimina la joya temporal.
- Si se ejecuta una parte de la colección, las peticiones de limpieza se omiten.

## Comportamientos esperados

- `Login con clave incorrecta` y `Acceso sin token` devuelven **401**; **403** corresponde a un rol insuficiente.
- `Detalle resiliente` devuelve **200** o **503** si el circuito está abierto.
- El stock se descuenta de forma asíncrona; la verificación admite el valor esperado tras Kafka y Rabbit.
- Si Kafka está caído, `POST /sales` responde **503** y no guarda la venta.
- Si inventario está caído, `POST /sales` y los reportes responden **503**.

## Ejecución por consola

```powershell
npx --yes newman run "postman/Joyeria-Morgan-DEMO.postman_collection.json" --reporters cli
```
