# Joyería Morgan

Aplicación web para gestionar inventario, clientes, ventas y reportes de una joyería. Backend con microservicios Spring Boot y frontend Angular.

## Tecnologías

Java 17, Spring Boot 3.2.5, Spring Cloud, Spring Security, JWT, MySQL 8, RabbitMQ, Kafka, Angular 22, Docker y Kubernetes.

## Servicios

| Componente | Puerto | Función |
| --- | ---: | --- |
| `auth-service` | 8083 | Login con JWT y usuarios |
| `clientes-service` | 8084 | CRUD de clientes |
| `inventario-service` | 8081 | CRUD de joyas y stock |
| `ventas-service` | 8082 | Ventas, Feign, Circuit Breaker y eventos |
| `reportes-service` | 8085 | Reportes con datos de los otros servicios |
| `eureka-server` | 8761 | Registro de servicios |
| `config-server` | 8888 | Configuración centralizada |
| `api-gateway` | 8762 | Entrada al backend y CORS |
| `joyeria-front` | 4200 | Interfaz Angular |
| MySQL / RabbitMQ / Kafka | 5510 / 5672 / 9092 | Datos y mensajería |

## Requisitos

- Docker Desktop.
- Opcional para desarrollo local: JDK 17, Node.js y npm.

## Inicio rápido

```powershell
docker compose -f docker-compose.full.yml up -d --build
```

Abrir:

- Frontend: http://localhost:4200
- Eureka: http://localhost:8761
- Kafka UI: http://localhost:8090
- RabbitMQ: http://localhost:15672

Credenciales:

- `admin` / `admin123`
- `vendedor` / `vendedor123`
- RabbitMQ: `guest` / `guest`

## Ejecución local

1. Levantar MySQL, RabbitMQ, Kafka y Kafka UI:

```powershell
docker compose -f docker-compose.full.yml up -d mysql rabbitmq kafka kafka-ui
```

2. Iniciar en orden `eureka-server`, `config-server`, los servicios de `backend/` y `api-gateway`.

3. Iniciar el frontend:

```powershell
cd frontend/joyeria-front
npm install
npm start
```

Los servicios usan Config Server si está disponible; si no, arrancan con su configuración local. En desarrollo el frontend llama a los puertos `8081` a `8085`; en Docker usa nginx y `/api`.

## Funcionamiento

- `ventas-service` consulta `inventario-service` con Feign para validar productos y calcular totales.
- `POST /sales` publica en Kafka y descuenta stock; si Kafka falla, la venta no se confirma.
- `POST /sales/rabbit-reserve` reserva stock por RabbitMQ.
- Al eliminar una venta se devuelve el stock mediante un evento de compensación.
- `POST /sales/batch` registra un carrito completo con una sola boleta.
- Los servicios validan JWT: `401` sin token y `403` por rol insuficiente.
- Los reportes devuelven `503` si una dependencia no responde.

## API principal

| Recurso | Endpoints |
| --- | --- |
| Autenticación | `POST /auth/login`, `GET/POST/PUT/DELETE /users` |
| Inventario | `GET/POST /products`, `GET/PUT/DELETE /products/{id}` |
| Clientes | `GET/POST /customers`, `GET/PUT/DELETE /customers/{id}` |
| Ventas | `GET/POST /sales`, `GET/PUT/DELETE /sales/{id}`, `POST /sales/batch`, `POST /sales/rabbit-reserve`, `GET /sales/{id}/details` |
| Reportes | `GET /reports/resumen`, `GET /reports/inventario`, `GET /reports/ventas` |
| Salud | `GET /actuator/health` |

## Base de datos

MySQL expone `joyeria_db` en el puerto `5510`. JPA crea y actualiza las tablas `joyas`, `ventas`, `app_users` y `customers`. Los datos iniciales se cargan con las clases `*DataInitializer`.

## Pruebas

Frontend:

```powershell
cd frontend/joyeria-front
npm test
npm run build
```

Backend, dentro de cada servicio:

```powershell
.\gradlew.bat test
```

La suite del frontend tiene 7 pruebas. La colección de Postman tiene 39 peticiones en 9 carpetas y no requiere un environment:

```powershell
npx --yes newman run "postman/Joyeria-Morgan-DEMO.postman_collection.json" --reporters cli
```

## Docker y Kubernetes

El stack completo se levanta con `docker-compose.full.yml`. Los manifiestos de `k8s/` despliegan MySQL, Eureka, Config Server, los servicios y el gateway.

```powershell
kubectl apply -f k8s/
```

NodePorts: `30081` a `30085`, `30061`, `30062`, `30088` y `30306`.

## Estructura

```text
backend/      Servicios Spring Boot
infra/        Eureka, Config Server y API Gateway
frontend/     Aplicación Angular
database/     Scripts y compose de MySQL
postman/      Colección de pruebas
k8s/          Manifiestos de Kubernetes
```
