# CODE PRUEBA TECNICA BACKEND

**Autor:** Jose Gabriel Cortazar Ocampo

## Descripción

**Franchises API**
Este repositorio contiene el codigo de un API-REST reactiva para la gestión de franquicias, sucursales y productos,
desarrollada con Spring Boot WebFlux y MongoDB Atlas.

---

## Tecnologías

- Java 17
- Spring Boot 3 + WebFlux (programación reactiva)
- MongoDB Atlas (persistencia reactiva con ReactiveMongoRepository)
- Docker
- JUnit 5 + Mockito + StepVerifier (pruebas unitarias)

---

## Arquitectura

El proyecto está estructurado siguiendo los principios de **Clean Architecture**,
separando el dominio, los casos de uso y la infraestructura en capas independientes.

- **Domain** — modelos de negocio e interfaces de repositorio (sin dependencias externas)
- **Application** — casos de uso con la lógica de negocio
- **Infrastructure** — adaptadores de entrada (handlers/routers HTTP) y salida (MongoDB)

---

## Endpoints

Se anexa el archivo `API-Franchises.postman_collection.json` para importar en Postman y facilitar la prueba de los endpoints.

### Franquicias

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/franchises` | Crear franquicia |
| GET | `/franchises/{id}` | Buscar por ID |
| GET | `/franchises` | Listar todas |

### Sucursales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/branches` | Crear sucursal (franchiseId en el body) |
| GET | `/branches/{id}` | Buscar por ID |
| GET | `/branches/{franchiseId}/franchise` | Listar por franquicia |
| GET | `/branches` | Listar todas |

### Productos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/products` | Crear producto (branchId en el body) |
| GET | `/products/{id}` | Buscar por ID |
| GET | `/products/{branchId}/branch/` | Listar por sucurasales |
| GET | `/products` | Listar todos |
| PATCH | `/products/{id}/stock` | Actualizar stock |
| DELETE | `/products/{id}` | Eliminar producto |

### Consultas especiales

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/franchises/{franchiseId}/top-stock` | Producto con mayor stock por sucursal para una franquicia |

---

## Ejecutar localmente

### Requisitos previos

- Java 17
- Maven
- Docker

### Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `MONGODB_URI` | URI de conexión a MongoDB | `mongodb://localhost:27017/franchises` |
| `PORT_SERVER` | Puerto del servidor | `8080` |
| `ENV` | Perfil de Spring activo | — |

### Opción 1 — Con Docker Compose MongoDB Local

Requiere tener MongoDB corriendo localmente en el puerto `27017`.

Levanta la API y conecta con MongoDB local:

```bash
docker compose up
```

La API queda disponible en `http://localhost:8080`.

### Opción 2 — Con Docker Compose MongoDB Externo

Levanta la API y conecta con MongoDB-Atlas :

```bash
docker compose -f docker-compose.prod.yml up
```

La API queda disponible en `http://localhost:8090`.


### Opción 3 — Maven directo

Requiere tener MongoDB corriendo localmente en el puerto `27017`.

Levanta la API y conecta con MongoDB local:

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8060`.

---

## Ejecutar pruebas unitarias

```bash
mvn test
```

Las pruebas cubren las capas de UseCase y Handler para las tres entidades
(Franchise, Branch, Product), usando StepVerifier para validar flujos reactivos.

---

## Infraestructura como código

El aprovisionamiento de la infraestructura en Azure (AKS + ACR) y el despliegue
automatizado con Ansible se gestionan en un repositorio separado:

**[IaC — franchises-iac](https://github.com/josegcort/franchises_iac)**

---

## Consideraciones de seguridad

- El cluster de MongoDB Atlas está configurado con acceso abierto (`0.0.0.0/0`)
  para facilitar la evaluación. En un entorno productivo se restringiría
  el acceso únicamente a las IPs del servidor de aplicación.
---
