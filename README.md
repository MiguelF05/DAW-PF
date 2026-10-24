# Joyería Morgan

Aplicación web para la gestión de inventario, clientes, ventas y reportes de una joyería. El backend está construido con microservicios Spring Boot y el frontend con Angular.

## Arquitectura

| Componente | Puerto | Responsabilidad |
| --- | ---: | --- |
| `auth-service` | 8083 | Login con JWT, usuarios y contraseñas BCrypt |
| `clientes-service` | 8084 | CRUD de clientes |
| `inventario-service` | 8081 | CRUD de joyas, control de stock y consumo de eventos |
| `ventas-service` | 8082 | Registro de ventas, Feign, Circuit Breaker y publicación de eventos |
| `reportes-service` | 8085 | Resumen de inventario, ventas y clientes mediante Feign |
| `eureka-server` | 8761 | Registro y descubrimiento de servicios |
| `config-server` | 8888 | Configuración centralizada con repositorio nativo |
| `api-gateway` | 8762 | Enrutamiento, CORS y punto de entrada al backend |
| `joyeria-front` | 4200 | Interfaz Angular |
| MySQL | 5510 | Base de datos `joyeria_db` |
| RabbitMQ | 5672 / 15672 | Colas de reserva y alertas de stock |
| Kafka | 9092 | Topic `stock-movements` |
| Kafka UI | 8090 | Panel de inspección de Kafka |

Tecnologías principales: Java 17, Spring Boot 3.2.5, Spring Cloud, Spring Security, JWT, Feign, Resilience4j, RabbitMQ, Kafka, MySQL 8, Angular 22, Docker y Kubernetes.

## Estructura

```text
backend/
  auth-service/
  clientes-service/
  inventario-service/
  ventas-service/
  reportes-service/
infra/
  eureka-server/
  config-server/
  api-gateway/
frontend/
  joyeria-front/
database/
  docker-compose.yml
  script.sql
postman/
  Joyeria-Morgan-DEMO.postman_collection.json
  README-coleccion.md
k8s/
docker-compose.full.yml
```

## Requisitos

- Docker Desktop para la ejecución con contenedores.
- JDK 17 si los servicios se ejecutan desde IntelliJ o desde Gradle.
- Node.js y npm si el frontend se ejecuta con `ng serve`.
- Opcional: kubectl y un clúster de Kubernetes para los manifiestos de `k8s/`.

## Puesta en marcha

### Opción A: stack completo con Docker

```powershell
docker compose -f docker-compose.full.yml up -d --build
```

Cuando los contenedores estén disponibles, abre:

- Frontend: http://localhost:4200
- Eureka: http://localhost:8761
- Config Server: http://localhost:8888/api-gateway/default
- Kafka UI: http://localhost:8090
- RabbitMQ: http://localhost:15672

Credenciales de la aplicación:

- Administrador: `admin` / `admin123`
- Vendedor: `vendedor` / `vendedor123`
- RabbitMQ: `guest` / `guest`

### Opción B: infraestructura en Docker y servicios en IntelliJ

1. Levanta MySQL, RabbitMQ, Kafka y Kafka UI:

   ```powershell
   docker compose -f docker-compose.full.yml up -d mysql rabbitmq kafka kafka-ui
   ```

2. Inicia los servicios en este orden:
   `infra/eureka-server`, `infra/config-server`, los cinco servicios de `backend/` y `infra/api-gateway`.

3. Ejecuta el frontend:

   ```powershell
   cd frontend/joyeria-front
   npm install
   npm start
   ```

El frontend de desarrollo usa `environment.development.ts` y llama directamente a los puertos `8081` a `8085`. El build de producción usa `environment.ts` con `apiUrl: '/api'`; nginx redirige `/api/*` al API Gateway.

## Configuración

La configuración centralizada está en `infra/config-server/config-repo/`. Los servicios la importan con:

```text
optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}
```

Si el Config Server no está disponible, cada servicio arranca con su `application.yml` local. En Docker, los hosts y credenciales se inyectan mediante variables de entorno definidas en `docker-compose.full.yml`.

## Base de datos

La base se llama `joyeria_db` y se expone en el puerto `5510`. JPA crea y actualiza las tablas al iniciar los servicios:

- `joyas`
- `ventas`
- `app_users`
- `customers`

Los datos iniciales se cargan desde las clases `*DataInitializer` cuando las tablas están vacías. `database/script.sql` contiene inserts de referencia comentados.

## Flujos principales

- **Feign:** `ventas-service` consulta `inventario-service` para validar productos y calcular totales. Los endpoints `GET /sales/{id}/details` y `GET /sales/{id}/details-resiliente` exponen el detalle.
- **Kafka:** `POST /sales` publica un evento en `stock-movements`; `inventario-service` descuenta el stock. La venta solo se confirma cuando Kafka acepta el evento.
- **RabbitMQ:** `POST /sales/rabbit-reserve` usa `stock-reserve-queue`; las alertas de stock bajo se publican en `stock-low-queue`.
- **Compensación:** al eliminar una venta se publica `sale-cancellation-requests` y `inventario-service` devuelve el stock de cada producto.
- **Checkout del carrito:** `POST /sales/batch` recibe varios productos, valida todo el stock antes de guardar y crea una fila por producto con el mismo `numeroBoleta`.
- **Seguridad:** JWT en los cinco servicios, BCrypt para contraseñas, `401` sin token y `403` cuando el rol no tiene permiso.
- **Reportes:** `reportes-service` agrega datos de inventario, ventas y clientes. Si una dependencia no responde, devuelve `503` en lugar de ceros.

## API principal

| Servicio | Endpoints |
| --- | --- |
| Autenticación | `POST /auth/login`, `GET/POST/PUT/DELETE /users` |
| Inventario | `GET/POST /products`, `GET/PUT/DELETE /products/{id}` |
| Clientes | `GET/POST /customers`, `GET/PUT/DELETE /customers/{id}` |
| Ventas | `GET/POST /sales`, `GET/PUT/DELETE /sales/{id}`, `POST /sales/batch`, `POST /sales/rabbit-reserve`, `GET /sales/{id}/details`, `GET /sales/{id}/details-resiliente` |
| Reportes | `GET /reports/resumen`, `GET /reports/inventario`, `GET /reports/ventas` |
| Salud | `GET /actuator/health` |

## Pruebas

Backend, dentro de cada servicio:

```powershell
.\gradlew.bat test
```

Frontend:

```powershell
cd frontend/joyeria-front
npm test
npm run build
```

La suite del frontend tiene 7 pruebas.

La colección de Postman contiene 39 peticiones en 9 carpetas y no requiere un environment: las variables están definidas dentro de la colección. El detalle está en `postman/README-coleccion.md`.

```powershell
npx --yes newman run "postman/Joyeria-Morgan-DEMO.postman_collection.json" --reporters cli
```

## Docker y Kubernetes

Los Dockerfiles usan compilación multi-stage, por lo que no necesitan `build/` ni `target/` en la máquina. El stack completo se levanta con:

```powershell
docker compose -f docker-compose.full.yml up -d --build
```

Los manifiestos de `k8s/` despliegan MySQL, Eureka, Config Server, los cinco servicios, el gateway y sus Services. Los NodePort son `30081` a `30085`, `30061`, `30062`, `30088` y `30306`.

```powershell
kubectl apply -f k8s/
```
